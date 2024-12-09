//
//  ContentViewModel.swift
//  iosApp
//
//  Created by Godwin Olorunshola on 2024-12-08.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import shared
import KMPNativeCoroutinesAsync
import KMPNativeCoroutinesCore
import SwiftUI
import KMPNativeCoroutinesRxSwift
import Combine

enum ViewModelState {
    case idle
    case loading
    case success([User_])
    case failure(Error)
}

enum ViewModelAction {
    case fetchUsers(query: String, page: Int, perPage: Int)
    case fetchRecentSearches
}

@MainActor
final class ContentViewModel: ObservableObject {
    let koinApp: KoinApp = KoinApp()
    private var currentFetchUserTask: Task<Void, Never>?

    @Published private(set) var state: ViewModelState = .idle
    @Published var recentSearches: [RecentSearch] = []

    func send(action: ViewModelAction) async {
        switch action {
        case let .fetchUsers(query, page, perPage):
            currentFetchUserTask?.cancel()
            currentFetchUserTask = Task {
                try? await Task.sleep(nanoseconds: 300_000_000)
                
                guard !Task.isCancelled, !query.isEmpty else { return }
                await fetchUsers(query: query, page: page, perPage: perPage)
            }
        case .fetchRecentSearches:
            await fetchRecentSearches()
        }
    }
    
    private func fetchUsers(query: String, page: Int, perPage: Int) async {
        state = .loading
        do {
            var users = [User_]()
            let sequence = asyncSequence(
                for: koinApp.getUsers(query: query, page: Int32(page), perPage: Int32(perPage)))
            
            for try await fetchedUsers in sequence {
                for fetchedUser in fetchedUsers {
                    if let index = users.firstIndex(where: { $0.id == fetchedUser.id }) {
                        users[index] = fetchedUser
                    } else {
                        users.append(fetchedUser)
                    }
                }
            }
            state = .success(users)
        } catch {
            state = .failure(error)
        }
    }

    private func fetchRecentSearches() async {
        do {
            let sequence = asyncSequence(for: koinApp.getRecentSearches())
            for try await fetchResults in sequence {
                var uniqueRecentSearches = [RecentSearch]()
                var seenQueries = Set<String>()
                for result in fetchResults.prefix(5) {
                    if !seenQueries.contains(result.query) {
                        uniqueRecentSearches.append(result)
                        seenQueries.insert(result.query)
                    }
                }
                
                recentSearches = uniqueRecentSearches
            }
        } catch {}
    }
}
