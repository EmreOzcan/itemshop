# Gameplay Information

## Commands and Syntax

### Syntax of the `/buy` Command
All subcommands explained below require the `itemshop.buy` permission node.
| command syntax | description | permission node |
|----|----|----|
| `help` | Show help for buy ||
| `hand [Integer amount (1)]` | Buy items of the type you're currently holding | `itemshop.buy.hand` |
| `<Material material> [Integer amount (1)]` | Buy items of the type you specified | `itemshop.buy.material` |
| `multipliers` | See active multipliers | `itemshop.multipliers` |

### Syntax of the `/cost` Command
All subcommands explained below require the `itemshop.buy.cost` permission node.
| command syntax | description | permission node |
|----|----|----|
| `help` | Show help for cost ||
| `[Material material (*)] [Integer amount (1)]` | See the buy cost of a material | `itemshop.buy.cost` |
| `set <Material material> <double cost>` or `/setcost ...` | Set the bare buy cost of a material | `itemshop.buy.cost.set` |

\*: If omitted and the command issuer is a player, `material` will be the type of the item the player is currently holding.

### Syntax of the `/sell` Command
All subcommands explained below require the `itemshop.sell` permission node.
| command syntax | description | permission node |
|----|----|----|
| `` or `help` | Show help for sell ||
| `all\|inventory` | Sell all sellable items in your inventory | `itemshop.sell.inventory` |
| `hand [Integer maxAmount (2147483647)]` | Sell items of the type you're currently holding | `itemshop.sell.hand` |
| `material <Material material> [Integer maxAmount (2147483647)]` | Sell items of the type you specified | `itemshop.sell.material` |
| `multipliers` | See active multipliers | `itemshop.multipliers` |
| `<query>` | Search results for `query` ||

### Syntax of the `/worth` Command
All subcommands explained below require the `itemshop.sell.worth` permission node.
| command syntax | description | permission node |
|----|----|----|
| `help` | Show help for worth ||
| `[Material material (*)] [Integer amount (1)]` | See the sell worth of a material | `itemshop.sell.worth` |
|  `set <Material material> <double worth>` or `/setworth ...` | Set the bare sell worth of a material | `itemshop.sell.worth.set` |

\*: If omitted and the command issuer is a player, `material` will be the type of the item the player is currently holding.

### Synax of the `/itemshop` Command
All subcommands explained below require the `itemshop.reload` permission node.
| command syntax | description | permission node |
|----|----|----|
|| Reloads the config file | `itemshop.reload` |

## Default Configuration Files

### Default `item-values.yml`
```yaml
# Item values for Itemshop - item-values.yml

# Example: Players can sell stone for 0.8 each and buy it for 1.0 each.
stone:
  sell: 0.8
  buy: 1.0

# Example: Players can sell diorite for 3 but they cannot buy it.
diorite:
  sell: 3.0
  buy:

# Example: Players can buy andesite for 7 each but they cannot sell it.
andesite:
  sell:
  buy: 7.0

# Items not here cannot be sold or bought by default.
```

### Default `multipliers.yml`
```yaml
# Multipliers for Itemshop - multipliers.yml

# Example: Players with the permission "itemshop.m.free" will buy everything for free.
# (Item's buy cost is multiplied with "0.0".)
free: # This is the name used in permissions
  name: Communism # This is the name used in human readable texts
  buy: 0.0
  sell: 0.0

# Another example: Player with the permission "itemshop.m.tax" will sell items for 10% less and buy items for 10% more.
# (Item's buy cost is multiplied with "1.1" and sell value is multiplied with "0.9".)
tax:
  name: Tax
  sell: 0.9
  buy: 1.1

# Players without any of these permissions will sell and buy items with the default multiplier of "1.0".
# If you don't add a sell or buy for a modifier it will be assumed to be 1.
```

**Important Note:** Using the `sell worth set ...`, `buy cost set ...` commands or one of their aliases will remove the comments in the configuration files.

# Building From Source
Requirements:
* Gradle
* JDK 1.8 or later

1. Obtain the source code. Clone the git repo using `git clone git@git.sr.ht:~emre/itemshop`
2. Issue the following command: `gradle build`

The built plugin will be placed in `./build/libs/itemshop-<version>-all.jar`
