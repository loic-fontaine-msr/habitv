<?php phptemplate_comment_wrapper(NULL, $node->type); ?>

<?php if ($page == 0): ?>
  <div id="node-<?php print $node->nid; ?>" class="post node<?php if ($sticky) { print ' sticky'; } ?><?php if (!$status) { print ' node-unpublished'; } ?>">
  <div class="post-date"><span class="post-month"><?php print (format_date($node->created, 'custom', 'M')) ?></span> <span class="post-day"><?php print (format_date($node->created, 'custom', 'd')) ?></span></div>
  <div class="entry">
    <h2><a href="<?php print $node_url ?>" rel="bookmark" title="Permanent Link to <?php print $title ?>"><?php print $title ?></a></h2>
    <?php if ($taxonomy): ?><span class="post-cat"><?php print $terms ?></span><?php endif;?><?php if ($links): ?><span class="post-comments"><?php print $links; ?></span><?php endif; ?>
    <div class="post-content">
      <?php print $content ?>
    </div>
  </div> 
<?php endif; ?>
  
<?php if ($page == 1): ?>
  <div style="margin-top: -10px" id="node-<?php print $node->nid; ?>" class="post node<?php if ($sticky) { print ' sticky'; } ?><?php if (!$status) { print ' node-unpublished'; } ?>">
    <?php if ($taxonomy): ?><span class="post-cat"><?php print $terms ?></span><?php endif;?><span class="post-calendar"><?php print (format_date($node->created)) ?></span>
    <div class="post-content">
      <?php print $content ?>
      <?php print $links; ?>
    </div>    
<?php endif; ?>
</div>
<div class="clear"></div>