package com.nzarudna.shoppinglist.ui.templates.editdialog;

import com.nzarudna.shoppinglist.R;
import com.nzarudna.shoppinglist.model.template.CategoryTemplateItem;

/**
 * Created by Nataliia on 06.03.2018.
 */

public class EditTemplateDialogFragment extends BaseEditTemplateDialogFragment<CategoryTemplateItem, EditTemplateViewModel> {

    public static EditTemplateDialogFragment newInstance() {
        return new EditTemplateDialogFragment();
    }

    @Override
    protected int getDialogFragmentResID() {
        return R.layout.fragment_edit_template_dialog;
    }
}
