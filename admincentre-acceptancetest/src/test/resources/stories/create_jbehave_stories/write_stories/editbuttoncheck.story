Meta:

Narrative:
Create thucydides story and steps to check whether all config setting data can be edited.
Write thucydides story according to the UI logic to have ability to create neccesary testing steps;

Scenario: Check the edit button functionality on a Node and on a Key. 

When the user opens the default page
And clicks on element with id 'preferencesForm-treeTable-0-editButton'
And the user fills 'preferencesForm-treeTable-0_9-inputName' field with 'root'
Then wait until all animations on page completed

And clicks on element with id 'preferencesForm-treeTable-0_9-editOkButton'
Then wait until all animations on page completed

And clicks on element with id 'preferencesForm-treeTable-0_0-editButton'
Then wait until all animations on page completed

And the user fills 'preferencesForm-treeTable-0_9-inputName' field with 'TestKey'
And the user fills 'preferencesForm-treeTable-0_9-inputValue' field with 'TestValue'

When the user clicks on element with id/name/className 'preferencesForm-treeTable-0_9-selectTypeMenu_label'
When the user clicks on element with id/name/className 'preferencesForm-treeTable-0_9-selectTypeMenu_label'
And clicks on element with id/name/className 'preferencesForm-treeTable-0_9-editOkButton'

