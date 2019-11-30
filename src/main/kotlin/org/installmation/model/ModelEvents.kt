package org.installmation.model

class JDKListUpdatedEvent(val updated:Collection<NamedDirectory>)

/*
  Module library events - Java 9+ modules
 */
class ModuleLibUpdatedEvent(val updated:Collection<NamedDirectory>)

class ModuleLibSelectedEvent(val selected:NamedDirectory)

class ModuleLibDeselectedEvent(val deselected:NamedDirectory)

/*
  Jmod events - Java 9+ modules
 */
class ModuleJmodUpdatedEvent(val updated:Collection<NamedDirectory>)

class ModuleJmodSelectedEvent(val selected:NamedDirectory)

class ModuleJmodDeselectedEvent(val deselected:NamedDirectory)
