# Pesinari
An Android app written for a specific use-case,  this app simply forwards messages received to a phone to a email provided by the user

This app uses MailGun api (https://www.mailgun.com/) to send emails from the app, whenever a new sms receives it wakes up and sends a mail

<b><em>The app does not store messages either in the app locally or in the mail server </em></b>


TODO:
   -- Use encryption while sending mails
   -- Check if there is a better way to share messages than sending mails (which involves MailGun)