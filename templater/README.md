# templater

There are unit tests in the src/test folder. The project is build by Gralde.

### Usage examples:
```
What path would it be?
src/test/resources/BasicExample

Folder 2/File 2-1
File 2-1 contents

Folder 1/File 1-1
File 1-1 contents
require 'Folder 2/File 2-1'

Folder 2/File 2-2
require 'Folder 1/File 1-1'
require 'Folder 2/File 2-1'
File 2-2 contents
```

```
What path would it be?
src/test/resources/BasicCycle
Dependency graph contains cycles
```

```
What path would it be?
src/test/resources/BrokenRequire
Dependency graph contains unknown files
```