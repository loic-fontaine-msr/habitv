package com.dabi.habitv.plugin.email;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;

import com.dabi.habitv.api.plugin.api.PluginProviderInterface;
import com.dabi.habitv.api.plugin.dto.CategoryDTO;
import com.dabi.habitv.api.plugin.dto.EpisodeDTO;
import com.dabi.habitv.framework.plugin.api.BasePluginWithProxy;
import com.dabi.habitv.framework.plugin.tpl.TemplateIdBuilder;
import com.dabi.habitv.framework.plugin.tpl.TemplateUtils;

public class EmailPluginManager extends BasePluginWithProxy implements PluginProviderInterface {

	@Override
	public Set<EpisodeDTO> findEpisode(final CategoryDTO category) {
		MessageReceiver messageReceiver = buildMessageReceiver(category.getFatherCategory().getName(), category.getId());
		return messagesToEpisode(category, messageReceiver.getAllMessages());
	}

	private MessageReceiver buildMessageReceiver(String protocol, String command) {
		Map<String, String> params = TemplateUtils.getParamValues(command);
		if (EmailConf.IMAP.equals(protocol)) {
			return new IMAPMessageReceiver(params.get(EmailConf.HOST), params.get(EmailConf.USER), params.get(EmailConf.PASSWORD));
		} else {
			return new Pop3MessageReceiver(params.get(EmailConf.HOST), params.get(EmailConf.PORT), params.get(EmailConf.USER),
			        params.get(EmailConf.PASSWORD));
		}
	}

	private Set<EpisodeDTO> messagesToEpisode(CategoryDTO category, List<Map<String, String>> messages) {
		Set<EpisodeDTO> episodes = new HashSet<>();
		for (Map<String, String> message : messages) {
			try {
				EpisodeDTO episode = buildEpisodeFromMessage(category, message);
				if (episode != null) {
					episodes.add(episode);
				}
			} catch (Exception e) {
				getLog().error("failed to convert message to episode : " + message, e);
			}
		}
		return episodes;
	}

	private EpisodeDTO buildEpisodeFromMessage(CategoryDTO category, Map<String, String> message) {
		String id = buildId(message.get(EmailConf.CONTENT));
		String name = message.get(EmailConf.SUBJECT);
		return id == null || name == null ? null : new EpisodeDTO(category, name, id);
	}

	private String buildId(String content) {
		return firstLine(content.contains("<") ? html2text(content) : content);
	}

	private String firstLine(String string) {
		return string.split("\\r?\\n")[0];
	}

	private String html2text(String content) {
		return Jsoup.parse(content).text();
	}

	@Override
	public Set<CategoryDTO> findCategory() {
		final Set<CategoryDTO> categoryList = new LinkedHashSet<>();
		categoryList.add(TemplateUtils.buildCategoryTemplate(getName(), EmailConf.POP, buildPopTemplateID()));
		categoryList.add(TemplateUtils.buildCategoryTemplate(getName(), EmailConf.IMAP, buildIMAPTemplateID()));
		return categoryList;
	}

	private String buildPopTemplateID() {
		//@formatter:off
		TemplateIdBuilder templateIdBuilder = new TemplateIdBuilder()
				.addTemplateParam(EmailConf.HOST, "Serveur POP3 SSL", null)
				.addTemplateParam(EmailConf.PORT, "Port POP3 SSL", "995")
				.addTemplateParam(EmailConf.USER, "Utilisateur", null)
				.addTemplateParam(EmailConf.PASSWORD, "Mot de passe (stocké en clair)", null)
				.addComment("Saisissez les accès au compte POP 3 SSL.");
		//@formatter:on
		return templateIdBuilder.buildID();
	}

	private String buildIMAPTemplateID() {
		//@formatter:off
		TemplateIdBuilder templateIdBuilder = new TemplateIdBuilder()
				.addTemplateParam(EmailConf.HOST, "Serveur IMAP", null)
				.addTemplateParam(EmailConf.USER, "Utilisateur", null)
				.addTemplateParam(EmailConf.PASSWORD, "Mot de passe (stocké en clair)", null)
				.addComment("Saisissez les accès au compte IMAP..");
		//@formatter:on
		return templateIdBuilder.buildID();
	}

	@Override
	public String getName() {
		return EmailConf.NAME;
	}
}
