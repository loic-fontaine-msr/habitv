<?php phptemplate_comment_wrapper(NULL, $node->type); ?>

<?php if ($page == 0): ?>
	<?php echo file_get_contents("http://mendibil.fr/?q=tracker"); ?>
<?php endif; ?>
  
<?php if ($page == 1): ?>
  <div style="margin-top: -24px" id="node-<?php print $node->nid; ?>" class="post node<?php if ($sticky) { print ' sticky'; } ?><?php if (!$status) { print ' node-unpublished'; } ?>">
	<span class="author">Post&eacute; par <?php print theme('username', $node)."."?></span>
    <?php if ($taxonomy): ?><span class="post-cat"><?php print $terms ?></span><?php endif;?><span class="post-calendar"><?php print (format_date($node->created)) ?></span>
    <div class="post-content">
      <?php print $content ?>
      <?php print $links; ?>
    </div>    
<?php endif; ?>
</div>
<div class="clear"></div>