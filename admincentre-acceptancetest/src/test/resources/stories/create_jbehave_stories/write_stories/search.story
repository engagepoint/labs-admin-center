Scenario: Open admincentre
checking whether all configurations and nodes are present in WEB UI using search panel
checking for only name, for only value and for name & value presence

When the user opens the default page
And the user fills 'preferencesForm-s_man2' field with '13'
Then wait until all animations on page completed
When clicks on element by '/html/body/div/div/div[3]/div/div/form/div[2]/div/div[3]/button'
Then wait until page content animations completed
When the user fills 'preferencesForm-s_man2' field with ''
Then wait until page content animations completed
When the user fills 'preferencesForm-s_man' field with '11'
Then wait until page content animations completed
When clicks on element by '/html/body/div/div/div[3]/div/div/form/div[2]/div/div[3]/button'
Then wait until page content animations completed
When the user fills 'preferencesForm-s_man' field with ''
Then wait until page content animations completed
When the user fills 'preferencesForm-s_man2' field with ''
Then wait until page content animations completed
When the user fills 'preferencesForm-s_man' field with '12'
Then wait until page content animations completed
When the user fills 'preferencesForm-s_man2' field with '12'
When clicks on element by '/html/body/div/div/div[3]/div/div/form/div[2]/div/div[3]/button'
Then wait until page content animations completed