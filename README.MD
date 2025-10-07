![Twitch Live Announcer Logo](https://i.imgur.com/CJX46H5.png)

# Twitch Live Announcer

Twitch Live Announcer (formerly Twitch Live Notifier for Spigot) keeps your Minecraft community informed whenever their favourite Twitch channels start or stop streaming. The plugin polls the Twitch API on a configurable schedule, posts rich chat notifications across your network, and gives you fine grained control over how stream status updates are delivered.

## Requirements

- Java 11 or newer
- Spigot/Paper (or compatible) server implementation
- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) *(required for placeholders)*
- Twitch application credentials (client ID and secret)

## Key Features

- **Channel monitoring** – Track any number of Twitch channels with streamer ID caching to prevent duplicate announcements. Channels can be registered from commands or the configuration file.
- **Player linking** – Link multiple Twitch channels to the same Minecraft account either in-game (`/setchannel`) or from the console (`/stream link add`). Links can be listed or removed at any time.
- **Smart announcements** – Customise live/offline messages, automatically include clickable links via `%link%`, or broadcast a combined `%multistream%` button that opens Multistre.am/MultiTwitch (or a custom aggregator) for all currently live streamers.
- **Command automation** – Run global, per-channel, or repeating timed console commands when a stream goes live/offline. Commands support `%player%`, `%channel%`, `%title%`, and `%prefix%` tokens.
- **Flexible filters** – Ignore streams that do not match configured game titles or title keywords so only relevant broadcasts are announced.
- **BungeeCord support** – Broadcast rich update messages to every server in your network using the plugin messaging channel.
- **MySQL integration** – Persist currently active channels to a database so external services can react to live events.
- **Config backups & auto updates** – Automatically back up `config.yml`/`messages.yml`, keep them versioned with BoostedYAML, and receive console alerts when an update is available.
- **PlaceholderAPI expansion** – Provide `%tla_live_<player>%`, `%tla_status_string_<player>%`, and `%tla_status%` placeholders that reflect real-time Twitch status inside scoreboards, menus, or chat.
- **Quality-of-life options** – Delay announcements until linked players join, disable specific messages entirely, or remove invalid channels automatically to keep logs tidy.
- **bStats metrics toggle** – Opt in or out of anonymous usage metrics with a single configuration flag.

## Installation

1. Download the latest release from Modrinth or Spigot and place the jar in your server's `plugins` directory.
2. Restart the server to generate the default configuration files.
3. Edit `plugins/TwitchLiveAnnouncer/config.yml`:
   - Set `client_id` and `client_secret` using credentials from <https://dev.twitch.tv/console/apps>.
   - Add any starter channels under `channels:` (you can add more later with commands).
   - Adjust `reload_time`, filters, messaging, multi-stream options, command automation, MySQL, and Bungee settings to suit your server.
4. (Optional) Configure `messages.yml` to change all player-facing text.
5. Reload the plugin with `/stream reload` or restart the server.

## Configuration Highlights

- **Polling interval** – `reload_time` controls how often Twitch is queried (seconds).
- **Backup scheduler** – Enable `backupConfig` to archive config/messages daily or on startup.
- **Multi-stream broadcasts** – Turn on `multipleStreamService` to periodically advertise a combined viewing link; supports Multistre.am, MultiTwitch, or a custom URL builder.
- **Command execution** – Use `commands`, `channelCommands`, and `timedCommands` sections to issue console commands when streams change state or while they are live.
- **Filters** – Limit announcements by Twitch game (`filter-stream-type`) and/or title keywords (`filter-stream-title`).
- **Behaviour toggles** – Use `disable-streaming-message`, `disable-not-streaming-message`, and `announce-only-if-streamer-on-server` to fine tune how and when chat alerts fire.
- **MySQL** – Enable `mysql` to keep a table of currently live channels synced for use by websites, bots, or dashboards.
- **Bungee mode** – Flip `bungee.enabled` to relay notifications to every connected server via plugin messages.

## Commands & Permissions

| Command | Description | Permission |
| --- | --- | --- |
| `/stream` | Shows base usage/help text. | *(none)* |
| `/stream reload` | Reloads configuration and message files. | `twitchliveannouncer.reload` |
| `/stream forceCheck` | Immediately refreshes Twitch statuses. | `twitchliveannouncer.forceCheck` |
| `/stream list` | Displays all tracked channels with online/offline state. | `twitchliveannouncer.list` |
| `/stream channels add <twitch>` | Adds a channel to the tracking list. | `twitchliveannouncer.channels.add` |
| `/stream channels remove <twitch>` | Removes a channel from tracking. | `twitchliveannouncer.channels.remove` |
| `/stream channels list` | Lists every tracked channel. | `twitchliveannouncer.channels.list` |
| `/stream link add <player> <twitch>` | Links a Twitch channel to a Minecraft player. | `twitchliveannouncer.link.add` *(+ `twitchliveannouncer.link.multiple` to link more than one channel)* |
| `/stream link remove <player> <twitch>` | Unlinks a Twitch channel from a player. | `twitchliveannouncer.link.remove` |
| `/stream link clear <player>` | Clears all Twitch links for the player. | `twitchliveannouncer.link.clear` |
| `/stream link list` | Displays every player-to-channel link. | `twitchliveannouncer.link.list` |
| `/setchannel <twitch>` | Player command to link themselves to a Twitch channel (auto-adds to tracking list). | `twitchliveannouncer.setchannel` *(+ `twitchliveannouncer.link.multiple` to add more than one)* |
| `/clearchannel` | Player command to remove their own Twitch links. | `twitchliveannouncer.clearchannel` |

Aliases: `/tla` and `/twitch` mirror the `/stream` command.

## Placeholders

| Placeholder | Output |
| --- | --- |
| `%tla_live_<player>%` | `true`/`false` when any of the player's linked channels is live. |
| `%tla_status_string_<player>%` | Returns the configured string (e.g., "Live"/"Offline") based on Twitch status. |
| `%tla_status%` | Same as the status string placeholder but automatically resolves the viewer's own username. |
| `%link%` | Embed in broadcast messages to insert a clickable Twitch URL for the active channel. |
| `%multistream%` | Embed in broadcast messages to insert a clickable multi-stream URL when enabled. |

## Support & Contributions

- Configure everything with the in-game commands or edit the YAML files for deeper customisation.
- Issue reports and pull requests are welcome—check the GitHub repository for contribution guidelines.
- Additional guides, tips, and community help are available via the plugin's wiki and Discord (see the Modrinth/Spigot project page).

## Donations

If the plugin helps your community, consider supporting the development:

- [PayPal](https://paypal.me/ax3lt)
- [DonationAlerts](https://www.donationalerts.com/r/ax3lt)
