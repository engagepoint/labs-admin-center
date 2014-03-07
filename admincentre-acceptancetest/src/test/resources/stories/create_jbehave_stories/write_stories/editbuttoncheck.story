Meta:

Narrative:
Create thucydides story and steps to check whether all config setting data can be edited.
Write thucydides story according to the UI logic to have ability to create neccesary testing steps;

Scenario: Check the edit button functionality on a Node and on a Key. 

When the user opens the default page
And clicks on element with id 'editButton'
Then wait until all animations on page completed

And the user fills 'inputName' field with 'root'
Then wait until all animations on page completed

And clicks on element with id 'editOkButton'
Then wait until all animations on page completed

When the user clicks on element with id/name/className 'ui-icon-triangle-1-e'
Then wait until all animations on page completed

And clicks on element with id 'preferencesForm-treeTable-0_0-editButton'
Then wait until all animations on page completed

And the user fills 'preferencesForm-treeTable-0_9-inputName' field with 'TestKey'
Then wait until all animations on page completed

And the user fills 'preferencesForm-treeTable-0_9-inputValue' field with 'TestValue'
Then wait until all animations on page completed

When the user clicks on element with id/name/className 'preferencesForm-treeTable-0_9-selectTypeMenu_label'
When select "String" option in drop-down
And clicks on element with id/name/className 'itemString'
And clicks on element with id 'editOkButton'