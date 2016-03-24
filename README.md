# java-module-loader
Dynamic reloadable modules, hierarchical class loaders, loose coupling? Who needs jigsaw?

## User stories
**As a** software designer, **in order to** seperate concerns and clearly outline responsibilities, **I want to** be able to delineate my code into modules.

**As a** developer, **in order to** speed up the development feedback loop, **I want** modules to be individually reloadable after compilation with only the changed module and its dependent modules being affected.

## The goal 
is to have a module:
> An object with a public API and private (opaque/non-visible) implementation

That can be loaded with two class loaders:
> One for the public API (which can be used by other modules) and a private one that no other code gets to see or use.

That can be reloaded:
> e.g. when class files change

That have a lifecycle:
> i.e. can be started and stopped (triggering initialization and cleanup respectively)

That can have dependencies on other modules:
> i.e. can be wired up by a container (IoC) in a D.A.G.
