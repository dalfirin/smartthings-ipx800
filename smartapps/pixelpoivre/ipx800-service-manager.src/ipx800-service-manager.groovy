/**
 *  GCE IPX800 Service Manager
 *
 *  Copyright 2019 piXelPoivre
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
		name: "GCE IPX800 Service Manager",
		namespace: "pixelpoivre",
		author: "Ben Abonyi",
		description: "This smartapp installs the GCE IPX800 Manager App so you can add manage your board and associated modules",
		category: "Convenience",
		iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
		iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
		iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
		singleInstance: true
)


preferences {
    page(name: "mainPage", title: "Installation", install: true, uninstall: true) {
          section("Relay settings:"){
            input("RelayIP", "string", title:"Relay IP Address", description: "Please enter your relay's IP Address", required: true, displayDuringSetup: true)
            input("RelayPort", "string", title:"Relay Port", description: "Please enter your relay's HTTP Port", defaultValue: 80 , required: true, displayDuringSetup: true)
            input("RelayUser", "string", title:"Relay User", description: "Please enter your relay's username", required: false, displayDuringSetup: true)
            input("RelayPassword", "password", title:"Relay Password", description: "Please enter your relay's password", required: false, displayDuringSetup: true)
          }
          section("Extensions") {
            input("ExtensionType","enum", title: "Extensions", description: "Please select your extensions", required:false, submitOnChange: true,
              options: ["X-Dimmer", "X-Display", "X-8D", "X-GSM", "X-8R", "X-4VR", "X-DMX", "X-Eno", "X-24D", "X-4FP"], displayDuringSetup: true)
          }
          section("Hub Settings"){
            input("hubName", "hub", title:"Hub", description: "Please select your Hub", required: true, displayDuringSetup: true)
          }
    }
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {

  state.RelayIP = RelayIP
  state.RelayPort = RelayPort
  state.RelayUser = RelayUser
  state.RelayPassword = RelayPassword
  
  
  log.debug "Relay IP: ${state.RelayIP}"
  log.debug "Relay Port: ${state.RelayPort}"
  log.debug "Relay User: ${state.RelayUser}"
  log.debug "Relay Password: ${state.RelayPassword}"
  
  
  try {
    def DNI = (Math.abs(new Random().nextInt()) % 99999 + 1).toString()
    def Extensions = getChildDevices()
    if (Extensions) {
        Extensions[0].configure()
    }
    else {
      def childDevice = addChildDevice("pixelpoivre", ExtensionType, DNI, hubName.id, [name: app.label, label: app.label, completedSetup: true])
    }
  } catch (e) {
    log.error "Error creating device: ${e}"
  }
}

private removeChildDevices(delete) {
    delete.each {
        deleteChildDevice(it.deviceNetworkId)
    }
}
