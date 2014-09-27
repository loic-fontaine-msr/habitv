<div class="comment<?php print ($comment->new) ? ' comment-new' : ''; print ($comment->status == COMMENT_NOT_PUBLISHED) ? ' comment-unpublished' : ''; print ' '. $zebra; ?>">
<small class="commentmetadata">  
  <?php print t('!date', array('!date' => format_date($comment->timestamp))); ?>
  <?php if ($comment->new) : ?><a id="new"></a><span class="new"><?php print drupal_ucfirst($new) ?></span><?php endif; ?>
</small>
<cite><?php print t('!username', array('!username' => theme('username', $comment))); ?></cite> dit :
<br />
<?php print $content ?>
<?php if ($links): ?><div class="links"><?php print $links ?></div><?php endif; ?>
</div>
<br />