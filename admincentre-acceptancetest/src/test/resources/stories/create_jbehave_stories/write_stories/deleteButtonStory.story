Scenario: Add button check

When the user opens the default page

When clicks on element with id/name/className  'preferencesForm-treeTable-0-addButton'
When clicks on element with id/name/className  'preferencesForm-treeTable-0_0_2-addOkButton'
When the user fills 'preferencesForm-treeTable-0_0_2-addNameInput' field with 'ForDelete'
When the user fills 'preferencesForm-treeTable-0_0_2-addValueInput' field with 'VALUE'
When clicks on element with id/name/className  'preferencesForm-treeTable-0_0_2-addOkButton2'

Then wait until page content animations completed

Scenario: Delete button check
 
 When the user opens the default page

 When clicks on element with id/name/className  'preferencesForm-treeTable-0-addButton'
When clicks on element with id/name/className  'preferencesForm-treeTable-0_0_2-addOkButton'
When the user fills 'preferencesForm-treeTable-0_0_2-addNameInput' field with 'ForDelete'
When the user fills 'preferencesForm-treeTable-0_0_2-addValueInput' field with 'VALUE'
When clicks on element with id/name/className  'preferencesForm-treeTable-0_0_2-addOkButton2'
 
 Then wait until all animations on page completed