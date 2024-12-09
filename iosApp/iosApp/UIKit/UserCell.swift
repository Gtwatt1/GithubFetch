//
//  UserCell.swift
//  iosApp
//
//  Created by Godwin Olorunshola on 2024-12-08.
//  Copyright © 2024 orgName. All rights reserved.
//

import UIKit
import shared

class UserCell: UITableViewCell {

    static let identifier = "UserCell"

    private let avatarImageView = UIImageView()
    private let userNameLabel = UILabel()
    private let repoCountLabel = UILabel()

    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)

        setupUI()
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    private func setupUI() {
        avatarImageView.translatesAutoresizingMaskIntoConstraints = false
        avatarImageView.contentMode = .scaleAspectFill
        avatarImageView.layer.cornerRadius = 25
        avatarImageView.clipsToBounds = true
        contentView.addSubview(avatarImageView)

        userNameLabel.translatesAutoresizingMaskIntoConstraints = false
        userNameLabel.font = UIFont.boldSystemFont(ofSize: 16)
        contentView.addSubview(userNameLabel)

        repoCountLabel.translatesAutoresizingMaskIntoConstraints = false
        repoCountLabel.font = UIFont.systemFont(ofSize: 14)
        repoCountLabel.textColor = .gray
        contentView.addSubview(repoCountLabel)

        NSLayoutConstraint.activate([
            avatarImageView.leadingAnchor.constraint(equalTo: contentView.leadingAnchor, constant: 16),
            avatarImageView.centerYAnchor.constraint(equalTo: contentView.centerYAnchor),
            avatarImageView.widthAnchor.constraint(equalToConstant: 50),
            avatarImageView.heightAnchor.constraint(equalToConstant: 50),

            userNameLabel.leadingAnchor.constraint(equalTo: avatarImageView.trailingAnchor, constant: 16),
            userNameLabel.topAnchor.constraint(equalTo: contentView.topAnchor, constant: 8),
            userNameLabel.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -16),

            repoCountLabel.leadingAnchor.constraint(equalTo: userNameLabel.leadingAnchor),
            repoCountLabel.topAnchor.constraint(equalTo: userNameLabel.bottomAnchor, constant: 4),
            repoCountLabel.trailingAnchor.constraint(equalTo: contentView.trailingAnchor, constant: -16),
            repoCountLabel.bottomAnchor.constraint(equalTo: contentView.bottomAnchor, constant: -8)
        ])
    }

    func configure(with user: User_) {
        userNameLabel.text = user.userName
        repoCountLabel.text = "Repo Count: \(user.repoCount ?? 0)"
        avatarImageView.loadImage(user.avatarUrl)
    }

}


extension UIImageView {
    private static var imageCache = NSCache<NSString, UIImage>()
    
    func loadImage(_ urlString: String?) {
        guard let urlString,
              let url = URL(string: urlString) else { return }
        
        if let cachedImage = UIImageView.imageCache.object(forKey: urlString as NSString) {
            self.image = cachedImage
            return
        }
        let currentImage = self.image

        Task {
            do {
                let (data, _) = try await URLSession.shared.data(from: url)
                if let image = UIImage(data: data) {
                    UIImageView.imageCache.setObject(image, forKey: urlString as NSString)
                    DispatchQueue.main.async {
                        if self.image == currentImage {
                            self.image = image
                        }
                       
                    }
                }
            } catch {
                backgroundColor = .gray
            }
        }
    }
}
