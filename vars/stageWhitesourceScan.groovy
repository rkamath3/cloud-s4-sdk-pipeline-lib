import com.sap.piper.ConfigurationLoader

def call(Map parameters = [:]) {
    def stageName = 'whitesourceScan'
    def script = parameters.script
    runAsStage(stageName: stageName, script: script) {
        // Maven
        Map whitesourceConfiguration = ConfigurationLoader.stageConfiguration(script, stageName)
        if (whitesourceConfiguration) {
            def orgToken = whitesourceConfiguration.orgToken
            def product = whitesourceConfiguration.product

            executeWhitesourceScanMaven script: script, orgToken: orgToken, product: product, pomPath: 'application/pom.xml'
        } else {
            println('Skip WhiteSource Maven scan because the stage "whitesourceScan" is not configured.')
        }

        // NPM
        if (whitesourceConfiguration && fileExists('package.json')) {
            def orgToken = whitesourceConfiguration.orgToken
            def product = whitesourceConfiguration.product

            executeWhitesourceScanNpm(
                script: script,
                orgToken: orgToken,
                product: product,
            )
        } else {
            println 'Skipping WhiteSource NPM Plugin because no "package.json" file was found in project or the stage "whitesourceScan" is not configured.\n'
        }
    }
}
