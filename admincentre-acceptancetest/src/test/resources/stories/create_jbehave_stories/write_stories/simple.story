Scenario: Open admincentre


When the user opens the default page
And clicks on element with id 'preferencesForm-treeTable-0-editButton'
Then wait until all animations on page completed
And the user fills 'preferencesForm-treeTable-0-j_idt41' field with 'SomeKindOfNode'
Then wait until all animations on page completed
When clicks on element with id 'preferencesForm-treeTable-0-j_idt46'
Then wait until all animations on page completed
And clicks on element with id 'preferencesForm-treeTable-0_0-j_idt38'
Then wait until all animations on page completed
And clicks on element with id 'preferencesForm-treeTable-0_0_0-j_idt53'

