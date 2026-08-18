I want you to create a plan to build the functionality defined below into the new Better Portal Linking mod in this repository. Design the plan such that it can be executed by sub-agents using Sonnet, with a final check by an Opus sub-agent.

### Overall algorithm

* When an entity goes through a nether portal that is _not yet linked_ to one in the other dimension, check the corners of the frame of the entry portal
    * If one or more corner blocks belong to the `betterportallinking:portal_address_blocks` tag, proceed with these steps
    * If not, fall back to vanilla behavior
* Get all four corner blocks and store them in an array
    * Order does not matter
    * Duplicates are allowed
    * Air blocks are ignored
* When the game checks for an exit portal in the other dimension, do the following:
    * Get all active portals within range (no change to vanilla range at this time)
    * For each active portal:
        * Get the four corner blocks and check them against the `betterportallinking:portal_address_blocks` tag
        * Matches are stored as an array based on the same rules defined above
        * Save the array and portal "ID" (I don't know how Minecraft identifies a portal; this could be a UUID, block position, or something else) as a pair/tuple/something for use in the next step
* For each portal found that has one or more "address" blocks:
    * Compare the array against the array of blocks stored for the entry portal
        * The number of matching blocks is that portal's "score"
* If all portals have a score of "0", fall back to vanilla portal linking behavior; otherwise continue
* The portal with the highest score becomes the linked exit portal
* If two or more portals are tied for the highest score
    * The portal whose coordinates are closest to "entry portal coords / 8" becomes the new exit portal
    * If two or more portals are _still_ tied, choose one at random


### Possible mixin targets

Note: I don't know if the logic in these classes will _need_ to be modified, but based on their names, it seems plausible. Additionally, I couldn't find where these classes were called, so you will need to find the rest of the vanilla code having to do with portal generation and linking.

* `net.minecraft.world.level.portal.PortalForcer`
* `net.minecraft.world.level.portal.PortalShape`
