### Design Overview

* UI control is split across multiple JavaFX controllers
* There is a single 'application' controller (org.installmation.controller.InstallmationController)
* Controllers delegate implementation to Service classes as required
* All interactions between controllers and services is via events
* A single instance of Google Guava's EventBus mediates all events
* The user must create a new project before performing any work. This becomes the current project in the workspace.


### Domain Model - Key Classes

#### Configuration

This class contains all data common to all projects: such as JDKs and JavaFX installs for the system
on which the application is installed.

Persisted as JSON in the USER.HOME directory.

#### InstallCreator

Once a project is ready, this class does all the work, generating images, installer and scripts
depending on the choices made.

#### InstallProject

Each install is managed as a single InstallProject, which stores all the parameters and is persisted
to a json file.

Persisted in the USER.HOME directory.


#### Workspace

The application manages projects via a workspace (org.installmation.service.Workspace), which does
little right now but is planned to do more in future with regards to parameters and logic
common to multiple projects.

Persisted as JSON in the USER.HOME directory.

### Key Events

#### ProjectBeginSaveEvent

As the user enters data in different fields, this data is stored across multiple controllers, so when
a project needs to be saved, the project service emits a **ProjectBeginSaveEvent**, to which all
controllers subscribe, and add their data to the current project.

#### ProjectLoadedEvent

After the project service loads a project, it emits a **ProjectLoadedEvent**, to which all
controller subscribe and update their state to match the current project.








