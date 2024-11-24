package com.example.promptsharepro.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.example.promptsharepro.SearchHandler;
import com.example.promptsharepro.model.Post;

import java.util.ArrayList;
import java.util.List;

public class Feature3UnitTest {
    private SearchHandler searchHandler;
    private List<Post> testPosts;

    @Before
    public void setUp() {
        testPosts = new ArrayList<>();
        // Create test posts with distinct content
        Post post1 = new Post("1", "First Post", "This is a prompt for ChatGPT", "2024-03-23", "user1", "ChatGPT");
        Post post2 = new Post("2", "Second Post", "Tips for using Claude AI", "2024-03-23", "user2", "Claude");
        Post post3 = new Post("3", "Third Post", "General artificial intelligence discussion", "2024-03-23", "user3", "General");

        testPosts.add(post1);
        testPosts.add(post2);
        testPosts.add(post3);

        searchHandler = new SearchHandler(testPosts);
    }

    @Test
    public void testSearchByLLMKind() {
        // Test searching for ChatGPT posts
        List<Post> chatGPTResults = searchHandler.searchByLLMKind("ChatGPT");
        assertEquals("Should find 1 ChatGPT post", 1, chatGPTResults.size());
        assertEquals("Should find post with ChatGPT LLM", "ChatGPT", chatGPTResults.get(0).getLlmKind());

        // Test case-insensitive search
        List<Post> claudeResults = searchHandler.searchByLLMKind("claude");
        assertEquals("Should find 1 Claude post", 1, claudeResults.size());
        assertEquals("Should find post with Claude LLM", "Claude", claudeResults.get(0).getLlmKind());
    }

    @Test
    public void testSearchByTitle() {
        // Test exact title match
        List<Post> exactResults = searchHandler.searchByTitle("First Post");
        assertEquals("Should find 1 post with exact title", 1, exactResults.size());
        assertEquals("Should find correct title", "First Post", exactResults.get(0).getTitle());

        // Test partial title match
        List<Post> partialResults = searchHandler.searchByTitle("Post");
        assertEquals("Should find 3 posts with 'Post' in title", 3, partialResults.size());

        // Test case-insensitive search
        List<Post> caseInsensitiveResults = searchHandler.searchByTitle("post");
        assertEquals("Should find 3 posts with case-insensitive 'post'", 3, caseInsensitiveResults.size());
    }

    @Test
    public void testFullTextSearch() {
        // Test exact content match
        List<Post> promptResults = searchHandler.fullTextSearch("prompt for ChatGPT");
        assertEquals("Should find 1 post with exact content match", 1, promptResults.size());
        assertTrue("Content should contain search term", 
                  promptResults.get(0).getContent().toLowerCase().contains("prompt"));

        // Test partial content match
        List<Post> aiResults = searchHandler.fullTextSearch("ai");
        assertEquals("Should find 1 post with 'ai' in content", 1, aiResults.size());
        assertTrue("Content should contain 'ai'", 
                  aiResults.get(0).getContent().toLowerCase().contains("ai"));

        // Test case-insensitive content search
        List<Post> artificialResults = searchHandler.fullTextSearch("ARTIFICIAL");
        assertEquals("Should find 1 post with case-insensitive 'artificial'", 1, artificialResults.size());
        assertTrue("Content should contain 'artificial'", 
                  artificialResults.get(0).getContent().toLowerCase().contains("artificial"));
    }
}
