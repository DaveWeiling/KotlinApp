package com.pdog.plugin

import groovy.xml.QName
import org.gradle.api.Plugin
import org.gradle.api.Project

class ManifestClipper implements Plugin<Project> {

    static def MAIN_ACTIVITY = "com.android.learn.kotlin.MainActivity"


    static def MAIN_ACTION = "android.intent.action.MAIN"
    static def LAUNCHER_CATEGORY = "android.intent.category.LAUNCHER"
    static def NAME_WITH_NAME_SPACE = new QName("http://schemas.android.com/apk/res/android", "name", "android")

    @Override
    void apply(Project project) {
        println("ManifestClipper <<<<")

        def android = project.extensions.android
        android.applicationVariants.all { variant ->
            variant.outputs.all { output ->
                output.processManifest.doLast {

                    println("ManifestClipper <<<<")
                    // Stores the path to the maifest.
                    String manifestPath = "$manifestOutputDirectory/AndroidManifest.xml"
                    // Stores the contents of the manifest.
                    updateManifest(new File(manifestPath))
                }
            }
        }
    }

    static def updateManifest(File androidManifestFile) {
        def fileReader = new FileReader(androidManifestFile)
        def androidManifestXmlNode = new XmlParser().parse(fileReader)

        clipper(androidManifestXmlNode)

        // Write the manifest file
        def pw = new PrintWriter(androidManifestFile)
        new XmlNodePrinter(pw).print(androidManifestXmlNode)
    }

    static def clipper(Node androidManifest) {

        def components = androidManifest[new QName("application")][new QName("activity")]
        components.findAll { activity ->
            getNodeAttributeName(activity) != MAIN_ACTIVITY
        }.each { activity ->
            def intentFilters = activity[new QName("intent-filter")]

            intentFilters.each { intentFilter ->
                def actions = intentFilter[new QName("action")]
                def categories = intentFilter[new QName("category")]

                actions.each { action ->
                    def actionName = getNodeAttributeName(action)
                    if (actionName == MAIN_ACTION) {
                        intentFilter.remove(action)
                        println("remove action success  , at ${getNodeAttributeName(activity)}")
                    }
                }

                categories.each { category ->
                    def categoryName = getNodeAttributeName(category)
                    if (categoryName == LAUNCHER_CATEGORY) {
                        intentFilter.remove(category)
                        println("remove category success }, at ${getNodeAttributeName(activity)}")
                    }
                }

                // empty intent-filter node, we remove itself
                def childrenSize = intentFilter.children().size()
                if (childrenSize == 0) {
                    activity.remove(intentFilter)
                }
            }

        }
    }

    static def getNodeAttributeName(Node node) {
        return node.attribute(NAME_WITH_NAME_SPACE)
    }

    /*  private static void updateActivityNode(Node androidManifest, String key, String value) {
          def components = androidManifest.getAt(new QName("application")).getAt(new QName("activity"))
          def attributeKey = new QName("http://schemas.android.com/apk/res/android", key, "android")

          components.each {
              if (it.attribute(attributeKey) == null) {
                  it.attributes()[attributeKey] = value
              }
          }
      }*/
}