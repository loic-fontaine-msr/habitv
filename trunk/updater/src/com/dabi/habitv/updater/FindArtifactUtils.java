package com.dabi.habitv.updater;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.dabi.habitv.framework.plugin.utils.RetrieverUtils;

public class FindArtifactUtils {
	private static List<String> EXCLUDE = Arrays.asList("Parent Directory", "Name", "Last modified", "Size", "Description");

	public static class ArtifactVersion {
		private final String url;
		private final String version;

		private ArtifactVersion(final String url, final String version) {
			this.url = url;
			this.version = version;
		}

		public String getUrl() {
			return url;
		}

		public String getVersion() {
			return version;
		}

		@Override
		public String toString() {
			return "ArtifactVersion [url=" + url + ", version=" + version + "]";
		}

	}

	public static ArtifactVersion findLastVersionUrl(final String groupId, final String artifactId, final String coreVersion, final boolean autoriseSnapshot) {
		final String repo = "http://dabiboo.free.fr/repository";
		final String[] coreVersionSplit = coreVersion.split("\\.");
		final String versionMaj = coreVersionSplit[0] + "." + coreVersionSplit[1];
		final String groupIdUrl = groupId.replace(".", "/");
		final String artifactURL = repo + "/" + groupIdUrl + "/" + artifactId;

		return findLastVersionUrl(artifactURL, versionMaj, autoriseSnapshot);
	}

	private static ArtifactVersion findLastVersionUrl(final String artifactURL, final String versionMaj, final boolean autoriseSnapshot) {
		List<String> items = findItems(Type.DIR, artifactURL + "/");
		final String version = findLastVersion(versionMaj, items, autoriseSnapshot);
		final String artifactVersionUrl = artifactURL + "/" + version;
		items = findItems(Type.FILE, artifactVersionUrl);
		for (final String file : items) {
			if (file.endsWith(".jar")) {
				return new ArtifactVersion(artifactVersionUrl + "/" + file, version);
			}
		}
		return null;
	}

	private enum Type {
		FILE, DIR, ALL,
	}

	private static List<String> findItems(final Type type, final String url) {
		final org.jsoup.nodes.Document doc = Jsoup.parse(RetrieverUtils.getUrlContent(url, null));

		final Elements select = doc.select("a");

		final List<String> items = new LinkedList<>();
		if (!select.isEmpty()) {

			for (final Element aElement : select) {
				final String hRef = aElement.attr("href");
				final boolean isDirectory = isDirectory(hRef);
				if (!EXCLUDE.contains(hRef) && (type == Type.ALL || (type == Type.DIR && isDirectory) || (type == Type.FILE && !isDirectory))) {
					// System.out.println(text);
					items.add(hRef.replace("/", ""));
				}
			}
		}
		return items;
	}

	private static String findLastVersion(final String versionRef, final List<String> items, final boolean autoriseSnapshot) {
		Collections.sort(items);
		for (int i = items.size() - 1; i >= 0; i--) {
			final String version = items.get(i);
			if (version.startsWith(versionRef) && (autoriseSnapshot || !version.contains("SNAPSHOT"))) {
				return version;
			}
		}
		return null;
	}

	private static boolean isDirectory(final String attr) {
		return attr.endsWith("/");
	}
}
