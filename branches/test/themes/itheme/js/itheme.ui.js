/**
 * @author Eric Ferraiuolo
 */

$(document).ready(function(){
	$("div.dbx-box").each(function(){
		if($.cookie($(this).attr("id"))){
			$("div.dbx-content", this).hide();
		}
	});
	
	$("div.dbx-box h3.dbx-handle")
		.css({cursor:"pointer"})
		.click(function(){
			$(this).siblings("div.dbx-content").slideToggle("normal", function(){
				if($(this).css("display") == "none"){
					$.cookie($(this).parent("div.dbx-box").attr("id"), "hide", {path: cookiePath});
				}
				else if($.cookie($(this).parent("div.dbx-box").attr("id")) == "hide"){
					$.cookie($(this).parent("div.dbx-box").attr("id"), null);
				}
			});
		});
});