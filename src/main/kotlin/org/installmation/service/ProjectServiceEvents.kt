package org.installmation.service

import org.installmation.model.InstallProject


abstract class ProjectEvent(val project: InstallProject)

class ProjectCreatedEvent(p: InstallProject) : ProjectEvent(p)

class ProjectLoadedEvent(p: InstallProject) : ProjectEvent(p)

class ProjectUpdatedEvent(p: InstallProject) : ProjectEvent(p)

class ProjectSavedEvent(p: InstallProject) : ProjectEvent(p)

class ProjectDeletedEvent(p: InstallProject) : ProjectEvent(p)