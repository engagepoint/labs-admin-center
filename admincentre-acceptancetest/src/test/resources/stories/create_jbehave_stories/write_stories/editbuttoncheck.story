Scenario: Open admincentre
check whether all config setting data can be edited and    
define whether or not the changes can be saved and canceled


When the user opens the default page
And clicks on element with id 'editButton'
Then wait until all animations on page completed

And the user fills 'inputName' field with 'SomeKindOfNodeName'
Then wait until all animations on page completed
And clicks on element with id 'editOkButton'