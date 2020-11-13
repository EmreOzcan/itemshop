# Gameplay Information

## Commands and Syntax

### Syntax of the `/buy` Command
All subcommands explained below require the `itemshop.buy` permission node.
<table>
    <thead>
        <tr>
            <th>command syntax</th>
            <th>description</th>
            <th>permission node</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td><code>help</code></td>
            <td>Show help for buy</td>
            <td></td>
        </tr>
        <tr>
            <td><code>hand [Integer amount (1)]</code></td>
            <td>Buy items of the type you're currently holding</td>
            <td><code>itemshop.buy.hand</code></td>
        </tr>
        <tr>
            <td><code>&lt;Material material&gt; [Integer amount (1)]</code></td>
            <td>Buy items of the type you specified</td>
            <td><code>itemshop.buy.material</code></td>
        </tr>
        <tr>
            <td><code>multipliers</code></td>
            <td>See active multipliers</td>
            <td><code>itemshop.multipliers</code></td>
        </tr>
    </tbody>
</table>

### Syntax of the `/cost` Command
All subcommands explained below require the `itemshop.buy.cost` permission node.
<table>
    <thead>
        <tr>
            <th>command syntax</th>
            <th>description</th>
            <th>permission node</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td><code>help</code></td>
            <td>Show help for cost</td>
            <td></td>
        </tr>
        <tr>
            <td><code>[Material material (*)] [Integer amount (1)]</code></td>
            <td>See the buy cost of a material</td>
            <td><code>itemshop.buy.cost</code></td>
        </tr>
        <tr>
            <td><code>set &lt;Material material&gt; &lt;double cost&gt;</code> or <code>/setcost ...</code></td>
            <td>Set the bare buy cost of a material</td>
            <td><code>itemshop.buy.cost.set</code></td>
        </tr>
    </tbody>
</table>

\*: If omitted and the command issuer is a player, `material` will be the type of the item the player is currently holding.

### Syntax of the `/sell` Command
All subcommands explained below require the `itemshop.sell` permission node.
<table>
    <thead>
        <tr>
            <th>command syntax</th>
            <th>description</th>
            <th>permission node</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td>`` or <code>help</code></td>
            <td>Show help for sell</td>
            <td></td>
        </tr>
        <tr>
            <td><code>all|inventory</code></td>
            <td>Sell all sellable items in your inventory</td>
            <td><code>itemshop.sell.inventory</code></td>
        </tr>
        <tr>
            <td><code>hand [Integer maxAmount (2147483647)]</code></td>
            <td>Sell items of the type you're currently holding</td>
            <td><code>itemshop.sell.hand</code></td>
        </tr>
        <tr>
            <td><code>material &lt;Material material&gt; [Integer maxAmount (2147483647)]</code></td>
            <td>Sell items of the type you specified</td>
            <td><code>itemshop.sell.material</code></td>
        </tr>
        <tr>
            <td><code>multipliers</code></td>
            <td>See active multipliers</td>
            <td><code>itemshop.multipliers</code></td>
        </tr>
        <tr>
            <td><code>&lt;query&gt;</code></td>
            <td>Search results for <code>query</code></td>
            <td></td>
        </tr>
    </tbody>
</table>

### Syntax of the `/worth` Command
All subcommands explained below require the `itemshop.sell.worth` permission node.
<table>
    <thead>
        <tr>
            <th>command syntax</th>
            <th>description</th>
            <th>permission node</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td><code>help</code></td>
            <td>Show help for worth</td>
            <td></td>
        </tr>
        <tr>
            <td><code>[Material material (*)] [Integer amount (1)]</code></td>
            <td>See the sell worth of a material</td>
            <td><code>itemshop.sell.worth</code></td>
        </tr>
        <tr>
            <td><code>set &lt;Material material&gt; &lt;double worth&gt;</code> or <code>/setworth ...</code></td>
            <td>Set the bare sell worth of a material</td>
            <td><code>itemshop.sell.worth.set</code></td>
        </tr>
    </tbody>
</table>

\*: If omitted and the command issuer is a player, `material` will be the type of the item the player is currently holding.

### Synax of the `/itemshop` Command
All subcommands explained below require the `itemshop.reload` permission node.
<table>
    <thead>
        <tr>
            <th>command syntax</th>
            <th>description</th>
            <th>permission node</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td></td>
            <td>Reloads the config file</td>
            <td><code>itemshop.reload</code></td>
        </tr>
    </tbody>
</table>

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

Issue the command `gradle build`

The built plugin will be placed in `./build/libs/itemshop-<version>-all.jar`
