Scenario:
Doing simple ssh commands against the remote host
Given ssh client connected to host as user test with password test
Then I doing the ssh command ls /tmp -1a
When status of ssh command should be 0
And in the result of ssh command should be found "^.\n..\n" regex
Then I doing the ssh command uname -o
When status of ssh command should be 0
And the result of ssh command should be "GNU/Linux"
