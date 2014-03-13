Scenario: Open admincentre
check whether all config setting data can be added and
define whether or not the changes can be saved and canceled

When the user opens the default page
When the user clicks on element with id/name/className 'preferencesForm-treeTable-0-addButton'

Then wait until all animations on page completed
When the user clicks on element with id/name/className 'preferencesForm-treeTable-0_0-addOkButton'

And the user fills 'preferencesForm-treeTable-0_0-inputName2' field with 'ForDelete'
And the user fills 'preferencesForm-treeTable-0_0-inputValue2' field with 'ForDelete'
And the user clicks on element with id/name/className 'preferencesForm-treeTable-0_0-addOkButton2'
Then wait until all animations on page completed

When the user opens the default page
And the user clicks on element with id/name/className 'ui-icon-triangle-1-s'
Then wait until all animations on page completed

