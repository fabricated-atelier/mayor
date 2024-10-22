# Mayor

Overhauled village gameplay mod for Fabric 1.21.

### Installation
Mayor is a mod built for the [Fabric Loader](https://fabricmc.net/). It requires [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api) and [Cloth Config API](https://www.curseforge.com/minecraft/mc-mods/cloth-config) to be installed separately; all other dependencies are installed with the mod.

### License
Mayor is licensed under MIT.

### Datapacks
For each village structure an experience value and a price value can be set.  
An id can get set optional (so no need to put an id field in the json entry).  
The id field allows to set the level of a structure. The second number of the id defines its level: 
- ``structure_name_1_1`` -> `structure_name_1` is the translation key and it is level 1
- ``structure_name_1_2`` -> is level 2 and an upgrade of `structure_name_1_1`
- ``structure_name_1_3`` -> is level 3 and an upgrade of `structure_name_1_2`
- ``test_name_7236_3`` -> `test_name_7236` is the translation key and it is level 3, if no `test_name_7236_2` given, it can only be built when the village level is 3 or higher
- ``brrrr_5`` -> is automatically set to level 1 cause no second number was found

The folder path has to be ```data\modid\structure_data\YOURFILE.json```  
An example can be found below:

```json
{
  "plains_small_house_1": {
    "replace": false,
    "experience": 3000,
    "price": 24,
    "id": "plains_small_house_1" - optional
  }
}
```

---

<div style="text-align: center;">
<br>
<a href="https://fabricmc.net/"><img
    src="external/badges/supported_on_fabric_loader.png"
    alt="Supported on Fabric"
    width="200"
></a>
<a href="https://github.com/fabricated-atelier/mayor/issues"><img
    src="external/badges/work_in_progress.png"
    alt="Work in Progress"
    width="200"
></a>
</div>
