## URL Shortener using the Play Framework

This application prompts the user for a URL and provides a shortened redirect link.

The application uses the Play web framework with Scala and Slick, and is configured for a PostgreSql database backend. The application is ready to clone and deploy on heroku!

[Click Here](http://jamged.ml) for a running version (Forwarded through my jamged.ml domain for shorter URLs. Much cleaner looking than guarded-tor-3011.heroku.com).

**Note:** The application is extremely barebones and no frills when it comes to http views. I may come back and make everything look a little prettier in the future, but my primary goal was functionality :)

### Using the Application

The main view simply provides an http form asking the user to enter a URL they would like shortened. Doing so and submitting the form will present the user with a shortened URL that will redirect them to the originally entered URL.

Each redirect is logged in the database (user IP and a timestamp are saved for each redirect), and this information can be viewed by navigating to `{host}/{shortUrlString}/stats`. For example, if your shortened URL is `myHost.com/abc123`, then navigating to `myHost.com/abc123/stats` will display the timestamps of every redirect hit, grouped by IP address.

Includes a heroku Procfile and is configured to run as-is using the free heroku PostgreSql database that is provisioned for free with each new app.