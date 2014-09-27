$Id: README.txt,v 1.1.4.3 2007/04/29 14:43:18 fago Exp $

Usernode Module
------------------------
by Wolfgang Ziegler, nuppla@zites.net


This module tries to make users nodes. It cares for automatic creation and 
deletion of a node for each user, the so called usernode.

Features:

    * Use views to build user listings or even searches. Usernode provides an
      easy customizeable default view.
    * Use it instead of the user account page located at /user. The advantage is
      that you can theme it like any other node and
    * use features, which other modules provide for nodes, with the usernode.
      Think of comments, voting modules, taxonomy...

Notes: 
The usernode itself is empty. It's just a placeholder for its user.
The usernodes of blocked users are set to unpublished.
Manual deletion of usernodes (without deleting its user) 'll be prevented.


If you want to link to the usernode instead of linking to the old user page
as default you can override the functions theme_username() and 
theme_user_picture().
If you use a theme using the phptemplate engine just copy the template.php
file provided with this package to your theme's folder, or if the file 
already exists, only append the two functions inside the file to your 
existing file.


Installation 
------------
 * Copy the module to your modules directory and activate it.


Building userlists and searches
-------------------------------
The usernode module makes the user's data available in views. It provides an
easy customizeable default view 'userlist'.

You can extend your userlist with further information, which the user has entered
in a nodeprofile by using the views fusion module.
Have a look at http://drupal.org/project/views_fusion and its README.

