<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>ted show info</title>
</head>

<body bgcolor="#FFFFFF" text="#000000" link="#0000FF">

<?
	$tvcom = $_GET["tvcom"];
	$description = "";
	$showstatus = "";
	$showpremierdate = "";
	$showgenres ="";	
	$showwebsite = "";
	$showimage = "";
	
	$tvcom_url="http://www.tv.com/show/" . $tvcom . "/summary.html";
	
	$tvcom_content = getPageContent($tvcom_url);
	
	/*	// for performance sake: first find the summary
		$summary = getTagContents("<div id=\"show_summary\" class=\"module show_summary\">", "<script type=\"text/javascript\">", $tvcom_content);
		
		// next, find show data from summary
		if ($summary != "")
		{	
			// Get full description		
			$description = getTagContents("<span class=\"long\">", "</span>", $summary);
			
			if ($description == "")
			{
				// get the short summary, needed if there is no "full" summary
				$description = getTagContents("<span class=\"short\">",  "</span>", $summary);
			}
			
			// remove escaped characters
			$description = str_replace("\'", "'", $description);
			$description = str_replace("<br /><br /><br /><br />", "<br><br>", $description);
			
			$showscore = getTagContents("<span>Show Score</span>",  "<span class=\"description\">", $tvcom_content);
					
			$showstatus = getTagContents("<span class=\"program_status_name\">",  "</span>", $summary);
			$showpremierdate = getTagContents("<span class=\"start_date\">",  "</span>", $summary);
			$showgenres = getTagContents("<span class=\"genres\">",  "</span>", $summary);
			$showwebsite = getTagContents("<span class=\"official_site\">",  "</span>", $summary);			
		}
		*/
		
		// Get full description		
		$description = getTagContents("<p class=\"show_description MORE_LESS\">", "</p>", $tvcom_content);
		
		if ($description == "")
		{
			// get the short summary, needed if there is no "full" summary
			$description = getTagContents("<p id=\"trunc_summ\">", "<a class=\"show_hide", $tvcom_content);
		}
		
		// remove escaped characters
		$description = str_replace("<span class=\"truncater\">&hellip; <a class=\"show_more POINTER\" href=\"#\">More</a></span><span>", "", $description);
		$description = str_replace("\'", "'", $description);
		$description = str_replace("<br /><br /><br /><br />", "<br><br>", $description);
		
		$tvcom_header_img = "http://image.com.com/tv/images/content_headers/program_new/". $tvcom .".jpg";
		
		// get rating module
		$ratingmodule = getTagContents("<div class=\"MODULE MODULE_FIRST\" id=\"rate_it\">", "<span class=\"num_votes\">", $tvcom_content);
		// get rating
		$showscore = getTagContents("<span class=\"number\">", "</span>", $ratingmodule);
		$showscoredescription = getTagContents("<span class=\"description\">", "</span>", $ratingmodule);

		// get buzz
		$buzz = getTagContents("<div class=\"MODULE\" id=\"show_buzz_info\">", "</div>", $tvcom_content) . "</div>"; // add  div so next statement can filter out the buzz		
		// get rating and airdate from buzz
		$ratingstatusandpremierdate = "<h4>". getTagContents("<h4>", "</div>", $buzz); // append h4 for layout purposes
		$ratingstatusandpremierdate = str_replace("<h4>", "<br><br><b>", $ratingstatusandpremierdate); //remove h4 and add "b"
		$ratingstatusandpremierdate = str_replace("</h4>", "</b><br>", $ratingstatusandpremierdate); //remove h4 and add "b"
		
		function getPageContent($url)
		{
			$curl = curl_init();

		  // Setup headers - I used the same headers from Firefox version 2.0.0.6
		  // below was split up because php.net said the line was too long. :/
		  $header[0] = "Accept: text/xml,application/xml,application/xhtml+xml,";
		  $header[0] .= "text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5";
		  $header[] = "Cache-Control: max-age=0";
		  $header[] = "Connection: keep-alive";
		  $header[] = "Keep-Alive: 300";
		  $header[] = "Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7";
		  $header[] = "Accept-Language: en-us,en;q=0.5";
		  $header[] = "Pragma: "; // browsers keep this blank.

		  curl_setopt($curl, CURLOPT_URL, $url);
		  curl_setopt($curl, CURLOPT_USERAGENT, 'Googlebot/2.1 (+http://www.google.com/bot.html)');
		  curl_setopt($curl, CURLOPT_HTTPHEADER, $header);
		  curl_setopt($curl, CURLOPT_REFERER, 'http://www.google.com');
		  curl_setopt($curl, CURLOPT_ENCODING, 'gzip,deflate');
		  curl_setopt($curl, CURLOPT_AUTOREFERER, true);
		  curl_setopt($curl, CURLOPT_RETURNTRANSFER, 1);
		  curl_setopt($curl, CURLOPT_TIMEOUT, 10);

		  $html = curl_exec($curl); // execute the curl command
		  curl_close($curl); // close the connection

		  return $html; // and finally, return $html
		}
		
		// find string between $opentag and $closetag in $data		
		function getTagContents($opentag, $closetag, $data)
		{
			$result = "";
			$temp = "";
			
			// find opentag
			$temp = strstr($data, $opentag);
			// find closetag
			$endpos = strpos($temp, $closetag);
			// cut from opentag till closetag
			$result = substr ($temp, 0, $endpos);
				// remove open tag from result
			$result = substr($result, strlen($opentag));//str_replace ($opentag, "", $result);
			
			// change \> with >
			$result = str_replace("/>", ">", $result);
			
			return $result;
		}

?>
<table border="0" align="left" cellpadding="4" width="480">
<?
if ($tvcom_header_img != "")
{
	?>
	<tr valign="top" width="480">
		<td align="center" valign="top" bgcolor="#E9E9E9" colspan="2">
			<img src=<?echo($tvcom_header_img)?> border=0>
		</td>
	</tr>
	<?
}
?>
<tr>
	<td align="left" valign="top" bgcolor="#E9E9E9">
		<a href="<?echo($tvcom_url)?>"><img src="http://image.com.com/tv/images/tv_icons/icon_tv.gif" border="0"></a>
		<br><br>
			<?
			if ($showscore !== "")
			{
				?>
				<font face="Arial, Helvetica, sans-serif">
				<b>Rating</b><br>
				<center>
				<font size="7">
					<b><?echo($showscore)?></b>
				</font>
				<br>
				<font size="3"><?echo($showscoredescription)?></font>
				</font>
				</center>
				<hr>
				<?
			}

			if ($ratingstatusandpremierdate != "")
			{
				?><font face="Arial, Helvetica, sans-serif"><?
				echo($ratingstatusandpremierdate);
				?></font><?
			}
			if ($showwebsite !== "")
			{
				?>
				<font size="2" face="Arial, Helvetica, sans-serif">
				<b>Official site</b><br>
				<?echo($showwebsite)?>
				</font>
				<hr>
				<?
			}
			
			?>
	  </font>	
	</td>
	<td valign="top" rowspan="2">
		<font size="3" face="Arial, Helvetica, sans-serif">
			<?echo($description);?>			
		</font>
	</td>
</tr>
<tr>
	<td valign="bottom" bgcolor="#E9E9E9">
		<font size="1" face="Arial, Helvetica, sans-serif">
			Used with permission from CNET Networks, Inc., All rights reserved.
		</font>
	</td>
</tr>
</table>

</body>
</html>