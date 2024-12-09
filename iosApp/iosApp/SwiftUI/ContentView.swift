import SwiftUI
import shared

struct ContentView: View {
    @StateObject var viewModel = ContentViewModel()
    @State var searchQuery = ""

    var body: some View {
        NavigationView {
            VStack {
                searchTextField
                recentSearchView
                contentView
                Spacer()
            }.task{
                await viewModel.send(action: .fetchRecentSearches)
            }.navigationTitle("User List")
                .navigationBarTitleDisplayMode(.inline)
                .navigationBarItems(trailing: NavigationLink(destination: ContentViewControllerWrapper()) {
                    Text("Go to UIKit")
                        .font(.headline)
                        .padding(10)
                })
        }
    }
    
    // MARK: - Subviews
    
    private var searchTextField: some View {
        TextField("Search for users...", text: $searchQuery)
            .padding()
            .textFieldStyle(RoundedBorderTextFieldStyle())
            .padding(.horizontal)
            .onChange(of: searchQuery) { query in
                fetchUsers(query: query)
            }
    }
    
    private var recentSearchView: some View {
        Group {
            if !viewModel.recentSearches.isEmpty {
                VStack(alignment: .leading, spacing: 8) {
                    recentSearchHeader
                    recentSearchList
                }
                .padding(.vertical)
            }
        }
    }
    
    private var recentSearchHeader: some View {
        Text("Recent Searches")
            .font(.subheadline)
            .foregroundColor(.gray)
            .padding(.horizontal)
    }
    
    private var recentSearchList: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 12) {
                ForEach(viewModel.recentSearches) { recentSearch in
                    recentSearchButton(recentSearch)
                        .transition(.opacity)
                       
                }
            }
            .padding(.horizontal)
        }
        .animation(.easeInOut, value: viewModel.recentSearches)
    }
    
    private func recentSearchButton(_ recentSearch: RecentSearch) -> some View {
        Button(action: {
            searchQuery = recentSearch.query
            fetchUsers(query: searchQuery)
        }) {
            Text(recentSearch.query)
                .font(.subheadline)
                .foregroundColor(.primary)
                .padding(.vertical, 6)
                .padding(.horizontal, 12)
                .background(Color.gray.opacity(0.1))
                .cornerRadius(20)
                .overlay(
                    RoundedRectangle(cornerRadius: 20)
                        .stroke(Color.gray.opacity(0.2), lineWidth: 1)
                )
        }
    }
    
    private var contentView: some View {
        Group {
            switch viewModel.state {
            case .idle:
                Text("Welcome! Start fetching users.")
            case .loading:
                ProgressView("Loading users...")
            case .success(let users):
                listView(users: users)
            case .failure(let error):
                Text("Error: \(error.localizedDescription)")
                    .foregroundColor(.red)
            }
        }
    }
    
    private func listView(users: [User_]) -> some View {
        List(users) { user in
            userRow(user: user)
        }.listStyle(PlainListStyle())
    }
    
    private func userRow(user: User_) -> some View {
        HStack(alignment: .top) {
            userAvatar(url: user.avatarUrl)
            userInfo(user: user)
        }
        .padding(.vertical, 8)
    }
    
    private func userAvatar(url: String) -> some View {
        AsyncImage(url: URL(string: url)) { image in
            image
                .resizable()
                .scaledToFit()
                .frame(width: 50, height: 50)
                .clipShape(Circle())
                .shadow(radius: 5)
        } placeholder: {
            ProgressView()
                .frame(width: 50, height: 50)
        }
    }
    
    private func userInfo(user: User_) -> some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(user.userName)
                .font(.headline)
            Text("Repo Count: \(user.repoCount ?? 0)")
                .font(.subheadline)
                .foregroundColor(.gray)
        }
    }


    private func fetchUsers(query: String) {
        Task {
            await viewModel.send(action: .fetchUsers(query: query, page: 1, perPage: 20))
        }
    }
}

extension User_: Identifiable {}
extension RecentSearch: Identifiable {}
