/*
 * Copyright 2019 Serge Merzliakov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.installmation.service

import org.installmation.model.InstallProject
import java.io.File




abstract class ProjectEvent(val project: InstallProject)

class ProjectCreatedEvent(p: InstallProject) : ProjectEvent(p)

class ProjectLoadingEvent(val projectFile: File)

class ProjectLoadedEvent(p: InstallProject) : ProjectEvent(p)

class ProjectUpdatedEvent(p: InstallProject) : ProjectEvent(p)

/**
 * Fire when a request to save the projech has been made
 */
class ProjectBeginSaveEvent(p: InstallProject) : ProjectEvent(p)

class ProjectSavedEvent(p: InstallProject) : ProjectEvent(p)

class ProjectClosedEvent(p: InstallProject) : ProjectEvent(p)

class ProjectDeletedEvent(p: InstallProject) : ProjectEvent(p)