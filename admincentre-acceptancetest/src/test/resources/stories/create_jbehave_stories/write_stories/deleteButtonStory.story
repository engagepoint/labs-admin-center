Scenario: Add button check

When the user opens the default page
And clicks on element with id/name/className 'ui-icon-triangle-1-e'
And clicks on element by '/html/body/div/div/div[3]/div/div/form/div[2]/table/tbody/tr[2]/td/span[2]/button'
Then wait until all animations on page completed
And clicks on element with id 'preferencesForm-treeTable-0_0_2-addButton'
And clicks on element with id 'preferencesForm-treeTable-0_0_2-addOkButton'
And the user fills 'preferencesForm-treeTable-0_0_2-j_idt68' field with 'ForDelete'
And the user fills 'preferencesForm-treeTable-0_0_2-j_idt70' field with 'VALUE'
And clicks on element with id 'preferencesForm-treeTable-0_0_2-addOkButton2'

Then wait until page content animations completed


Scenario: Delete button check

When the user opens the default page
And clicks on element with id/name/className 'ui-icon-triangle-1-e'
And clicks on element by '/html/body/div/div/div[3]/div/div/form/div[2]/table/tbody/tr[2]/td/span[2]/button'
And clicks on element by '/html/body/div/div/div[3]/div/div/form/div[2]/table/tbody/tr[5]/td/span[3]/button'

And clicks on element with id 'preferencesForm-treeTable-0_0_2_0-delButton'
And clicks on element with id 'preferencesForm-treeTable-0_0_2_0-delCancelButton'

And clicks on element with id 'preferencesForm-treeTable-0_0_2_0-delButton'
And clicks on element with id 'preferencesForm-treeTable-0_0_2_0-delOkButton'

Then wait until all animations on page completed