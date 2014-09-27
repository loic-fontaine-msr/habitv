<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" 
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="<?php print $language ?>" lang="<?php print $language ?>">
  <head>
	<title><?php print $head_title ?></title>
	<?php print $head ?>
	<?php print $styles ?>
	<?php print $scripts ?>
  </head>

<body>

	<div id="container">
		<div class="clear-block">
			<div id="header">
					<?php if ($search_box): print $search_box; endif; ?>
					
					<?php if ($logo) { ?><div class="logo"><a href="<?php print $base_path ?>" title="<?php print t('Home') ?>"><img src="<?php print $logo ?>" alt="<?php print t('Home') ?>" /></a></div><?php } ?>
					<?php if ($site_name) { ?><h1 class="site-name"><a href="<?php print $base_path ?>" title="<?php print t('Home') ?>"><?php print $site_name ?></a></h1><?php } ?>
					<?php if ($site_slogan) { ?><div class="site-slogan"><?php print $site_slogan ?></div><?php } ?>
			</div>

			<ul id="nav">
		        <?php if (isset($primary_links)) : ?>
		          <?php print theme('links', $primary_links, array('class' => 'links primary-links')) ?>
		        <?php endif; ?>
		        <?php if (isset($secondary_links)) : ?>
		          <?php print theme('links', $secondary_links, array('class' => 'links secondary-links')) ?>
		        <?php endif; ?>

			</ul>
		</div>
			
			<div><?php print $header ?></div>
			
			<div id="sidebar">
				<?php print $sidebar_left ?>
				<?php print $sidebar_right ?>
				<div id="sidebar_bottom">&nbsp;</div>
			</div>
			
			<div id="content">
				<?php if ($mission): print '<div id="mission">'. $mission .'</div>'; endif; ?>
			  
		        <?php if ($breadcrumb): print $breadcrumb; endif; ?>
				
				
				<?php if ($title): print '<h1 class="title">'. $title .'</h1>'; endif; ?>
	
				
				<?php if ($tabs): print '<div class="tabs">'. $tabs .'</div>'; endif; ?>
				
				<?php if (isset($tabs2)): print $tabs2; endif; ?>
	
				<?php if ($help): print $help; endif; ?>
				<?php if ($messages): print $messages; endif; ?>
				<?php print $content ?>
				<span class="clear"></span>
		      
			</div>

	</div>

	<div class="clear-block">
	
		<div id="footer">
		  <?php if ($footer_message): print '<p>'. $footer_message .'</p>'; endif; ?>
		  <p>&lt; Design by <a href="http://spellbook.infinitiv.it/">Gabriev</a> | Powered by <a href="http://www.drupal.org">Drupal</a> &gt;</p>
		</div>
		<?php print $closure ?>
		
	</div>

</body>
</html>
