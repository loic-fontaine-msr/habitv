<div class="dbx-box" id="<?php print 'block-'.$block->module.'-'.$block->delta ?>">

<?php if ($block->subject): ?>
  <h3 class="dbx-handle"><?php print $block->subject ?></h3>
<?php endif;?>

  <div class="dbx-content"><?php print $block->content ?></div>
</div>