# Donations
If you like my work, you can support me by donating to my [PayPal](https://paypal.me/ax3lt) account.<br>
Other donation link: [DonationAlerts](https://www.donationalerts.com/r/ax3lt)

# Twitch Live Notifier for Spigot

A Spigot plugin that broadcasts a message when a Twitch streamer goes live or offline. This plugin is still in early beta, so feel free to contribute to its development.

(I didn't test it completely, so if you find any bugs, please report them to me.)

- Requires PlaceholderAPI!

## Features
- Configurable messages for when a streamer goes live or offline.
- Streamer IDs are checked to prevent repeat messages (unless the streamer restarts their live stream).
- You can assign different channel names to one minecraft user
- You can run custom commands when the live goes online or offline
- You can filter the live category, showing only those that interest you
- Placeholder api:
  - %tla_live_<mcUsername>% responds with true or false if the streamer is live
  - %tla_status_string_<mcUsername>% shows a custom text based on whether the streamer is live or not
  - %tla_status% automatic user recognition
- Stream link: if you put %link% in now_streaming it will be replaced with the stream link (using a custom text)
- Server restart will result in the live message being shown again.

## Usage
1. Install the plugin on your Spigot server.
2. Configure the live and offline messages in the plugin's configuration file.
3. Add the Twitch streamers you want to monitor in the configuration file.
4. Restart your server and enjoy the live notifications!

## Contributing
If you are interested in contributing to this project, feel free to submit a pull request with your changes. Let's make this plugin the best it can be!

