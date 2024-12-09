# PromptShare Pro - Guide to Run the App

## Project Setup

1. **Clone the Repository**
   ```bash
   git clone https://github.com/your-username/PromptSharePro.git
   cd PromptSharePro
   ```

2. **Open in Android Studio**
   - Open **Android Studio** and load the project.
   - Ensure **Java JDK 8+** is installed.

3. **Configure Firebase**
   - Set up the project in **Firebase Console** and enable **Realtime Database** and **Email/Password Authentication**.
   - Download the `google-services.json` file and add it to the `app/` folder.

4. **Firebase Database Rules** (for testing):
   ```json
   {
     "rules": {
       ".read": "auth != null",
       ".write": "auth != null"
     }
   }
   ```

5. **Build the Project**
   - In Android Studio, go to **Build > Make Project** to compile.

---

## Running the App

1. **Use a Compatible Emulator or Device**
   - Choose an emulator with **API Level 25+** that supports **Google Play Store** or use a physical device with **USB Debugging** enabled.
   - Start the app by selecting **Run > Run 'app'** in Android Studio.

---

## Key Features to Test

The following features should be tested to ensure the app functions as described:

### 1. **User Registration and Login**

   - **Sign-Up Requirements**:
     - Users must use a **USC email** (ending with `@usc.edu`) and provide a **10-digit USC ID** to sign up.
   - **Login**:
     - Enter registered credentials to log in, view the main feed, and access user-specific functionalities.
   - **Validation**:
     - Sign-up and login processes include error feedback for incorrect email formats, invalid USC IDs, or non-existent accounts.

### 2. **Profile Management**

   - **Update Profile Information**:
     - Users can edit profile fields, including **email, USC ID, username, and password**.
     - Each edit provides real-time feedback on success or issues, such as duplicate usernames.
   - **Logout**:
     - A **Sign Out** button allows users to log out, ensuring session management and data security.

### 3. **Post Management**

   - **Create a Post**:
     - Users can create a post with the following fields:
       - **Title** (required): A description of the prompt.
       - **LLM Kind** (required): Selectable from predefined options.
       - **Content** (required): The actual prompt content.
       - **Author Notes**: Optional comments or instructions from the author.
     - Posts are added to the main feed in real-time.
   - **Edit and Delete Post**:
     - Users can **update or delete** their own posts directly from the main feed or post detail screen.
     - Updated posts retain the original post ID for continuity.

### 4. **Comment Management**

   - **Add Comments**:
     - Users can add comments on any post.
     - Required fields include:
       - **Rating**: Users rate the prompt from 1 to 5.
       - **Content**: Specific feedback or commentary on the post.
   - **Update and Delete Comments**:
     - Users can **update or delete** their own comments on any post.
     - Updated comments display changes immediately, ensuring an interactive user experience.

### 5. **Rating System for Comments**

   - Users can rate comments on posts, contributing a numeric value to show how helpful the comment is.
   - Ratings appear next to each comment, enhancing engagement and feedback.

### 6. **Search Functionality**

   - **Search by LLM Kind**:
     - Users can filter posts based on the **LLM type** (e.g., GPT-3.5, GPT-4).
   - **Search by Title**:
     - Enter a keyword to find posts whose titles contain the search term.
   - **Full-Text Search**:
     - Search within titles, contents, or author notes by providing a keyword.
     - Results dynamically update, with the latest matches displayed first.

### 7. **Viewing Other Users’ Posts**

   - The main feed displays posts from all users, sorted by the newest first.
   - Posts include essential details (title, author, timestamp), allowing users to explore shared prompts effectively.

### Improvement: 
##### After our sprint plan, we have added four addition features

   #### llm ranking
   
   Feature: Displays the average rating for each LLM type based on user ratings (out of five stars).
   Rationale: Provides users with a quick overview of highly-rated LLMs.
   
   #### User Post History
   
   Feature: Lists all posts made by a user under their profile.
   Rationale: Centralizes user activity tracking.
   
   #### Favorites/Collection List
   
   Feature: Allows users to "like" posts and save them to a collection accessible via their profile.
   Rationale: Facilitates easy access to valuable content.
   
   #### Subscription Sharing Section

   Feature: Enables users to share contact details to pool LLM subscription plans.
   Rationale: Encourages collaboration and cost-saving among users.


---

## Troubleshooting

- **Gradle Sync Issues**: Restart Android Studio, go to **File > Invalidate Caches/Restart** if sync fails.
- **Firebase Connection Errors**: Verify that `google-services.json` is added and Firebase Authentication is configured.
- **Emulator Compatibility**: Use an emulator with **API Level 25+** and **Google Play Store** for full functionality.
- **Network Issues**: Ensure a stable internet connection for Firebase sync.

---

This guide covers essential steps and feature checks to help you run, test, and evaluate PromptShare Pro. For any questions, please refer to the project documentation or contact the team members listed above.
