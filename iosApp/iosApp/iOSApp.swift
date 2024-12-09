import SwiftUI
import shared

@main
struct iOSApp: App {
    
    init() {
        KoinApp().doInitKoin()
    }
    
	var body: some Scene {
		WindowGroup {
            ContentView()
		}
	}
}


