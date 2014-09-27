<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="<?php print $language ?>" lang="<?php print $language ?>">
  <head profile="http://gmpg.org/xfn/11">
    <title><?php print $head_title ?></title>
    <?php print $head ?>
    <?php print $styles ?>
    <?php print $scripts ?>
    <style type="text/css" media="print">@import "<?php print base_path() . path_to_theme() ?>/print.css";</style>
  </head>
  <body>
<div id="page">
  <div id="header">
    <div id="headerimg">
	  <h1><a href="<?php print check_url($base_path); ?>"><?php print check_plain($site_name); ?></a></h1>
	  <div class="description"><?php print check_plain($site_slogan); ?></div>
	</div>
	
	<?php if (isset($primary_links)) : ?>
      <?php print theme('links', $primary_links, array('class' => 'nav')) ?>
    <?php endif; ?>
  </div>
  <div id="content">
    <?php if ($breadcrumb): print $breadcrumb; endif; ?>
    <?php if ($mission): print '<div id="mission">'. $mission .'</div>'; endif; ?>
    <?php if ($tabs): print '<div id="tabs-wrapper" class="clear-block">'. $tabs .'</div>'; endif; ?>
    <?php if (isset($tabs2)): print $tabs2; endif; ?>
    <?php if ($help): print $help; endif; ?>
    <?php if ($messages): print $messages; endif; ?>
    <?php if ($title): print '<h2'. ($tabs ? ' class="with-tabs"' : '') .'>'. $title .'</h2>'; endif; ?>
    <?php print $content ?>
    
	<div class="navigation">
	  <span class="previous-entries"></span> <span class="next-entries"></span>
	</div>	
		
  </div><!--/content -->
  
<div id="sidebar-right">
<ul>
  <li>
    <?php if ($search_box): ?><div class="block block-theme"><?php print $search_box ?></div><?php endif; ?>
  </li>
    <?php if ($sidebar_right): ?>
      <?php print $sidebar_right ?>
    <?php endif; ?>
</ul>
</div><!--/sidebar -->

  <hr class="clear" />
  <!--/header -->
</div>
<div id="credits">
<div class="alignright"><?php print $feed_icons ?></div>

</div>
<?php ini_set('display_errors', 1); ?>
  <?php print $closure ?>
  <script type="text/javascript">
var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
var pageTracker = _gat._getTracker("UA-404814-4");
pageTracker._trackPageview();
</script>
  </body>
</html>