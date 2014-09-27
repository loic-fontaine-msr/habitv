/* $Id: acidfree.js,v 1.6.2.1 2008/02/18 00:42:25 vhmauery Exp $ */

/*
Acidfree Photo Albums for Drupal
Copyright (C) 2005 Vernon Mauery

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
*/

/**
 * @file
 * this contains the javascript needed for acidfree to work nicely
 */

function set_thumb(base_url, select, id, count) {
    var img_base = base_url+'/acidfree/thumbnail/';
    var path;
    var img = document.getElementById('acidfree-thumb'+id);
    if (select.selectedIndex == 0) {
        path = img_base+select.options[Math.floor((Math.random()*count)+1)].value;
    } else {
        path = img_base+select.options[select.selectedIndex].value;
    }
    img.src = path;
}

function last_resized(large, small, thumb, last) {
    if (document.getElementById('edit-acidfree_large_dim').value != large ||
            document.getElementById('edit-acidfree_small_dim').value != small ||
            document.getElementById('edit-acidfree_thumb_dim').value != thumb) {
        document.getElementById('edit-acidfree_last_resized').value = 0;
        alert('last_resized = 0');
    } else {
        document.getElementById('edit-acidfree_last_resized').value = last;
        alert('last_resized = '+last);
    }
}

function select_nodes(select) {
    var form = select.form;
    var i;
    var sel = select.options[select.selectedIndex].value;
    for (i=0; i<form.length; i++) {
        if (form.elements[i].type == 'checkbox') {
		    // edit-nodes-0-checked
            var regex=/^edit-nodes-[0-9]+-checked/;
            if (regex.test(form.elements[i].id)) {
                if (sel == 'all')
                    form.elements[i].checked = true;
                else if (sel == 'none')
                    form.elements[i].checked = false;
                else if (sel == 'invert')
                    form.elements[i].checked ^= true;
            }
        }
    }
}

function set_title(file) {
    var title = document.getElementById('edit-title');
    if (title.value == '') {
        var t = file.toString();
        title.value = t.replace(/.*[\/\\](.*)\..*/i, '$1');
    }
}

function update_parent_selects(sel) {
    var form = sel.form;
    var i;
	var regex=/^edit-parent-[0-9]+/;
    for (i=0; i<form.length; i++) {
        if (form.elements[i] == sel)
            continue;
		if (regex.test(form.elements[i].id)) {
            form.elements[i].selectedIndex = sel.selectedIndex;
        }
    }
}

function toggle_album_select(chkbox) {
    var sel = document.getElementById('edit-acidfree-block-2-random-albums');
    if (chkbox.checked == true) {
        sel.disabled = true;
    } else {
        sel.disabled = false;
    }
}
function set_filename(file) {
	var filename = document.getElementById(file.id+'name');
	var t = file.value.toString();
	filename.value = t.replace(/.*[\/\\](.*)/i, '$1');
}
