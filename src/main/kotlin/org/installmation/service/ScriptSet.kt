package org.installmation.service

import org.installmation.model.GenerateResult
import org.installmation.model.ShellScript

class ScriptSet(val result: GenerateResult, val imageScript: ShellScript? = null, val installerScript: ShellScript? = null)