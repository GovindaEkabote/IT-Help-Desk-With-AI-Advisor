package com.help.desk.ai.prompt;


import org.springframework.stereotype.Component;

@Component
public class PromptBuilder {

    /**
     * CLASSIFICATION PROMPT - For categorizing tickets
     * Used when: User creates or updates a ticket
     */
    public static final String CLASSIFY_PROMPT =
            "You are an IT support ticket classifier. Classify this issue:\n\n" +
                    "Issue: %s\n\n" +
                    "Choose ONE category from these: HARDWARE, SOFTWARE, NETWORK, EMAIL, ACCESS\n" +
                    "Choose ONE priority from these: LOW, MEDIUM, HIGH, URGENT\n\n" +
                    "Rules:\n" +
                    "- Hardware: physical devices (printer, laptop, monitor, keyboard, mouse)\n" +
                    "- Software: applications (Excel, Word, browser, crash, install)\n" +
                    "- Network: connectivity (WiFi, VPN, internet, slow speed)\n" +
                    "- Email: specific email problems (Outlook, sending, receiving)\n" +
                    "- Access: permissions, login, roles\n\n" +
                    "Return ONLY this JSON format (no other text):\n" +
                    "{\"category\": \"CATEGORY\", \"priority\": \"PRIORITY\"}";

    /**
     * CHATBOT PROMPT - For answering user questions
     * Used when: User asks question via chatbot
     */
    public static final String CHATBOT_PROMPT =
            "You are an IT support assistant. Help with this question:\n\n" +
                    "Question: %s\n\n" +
                    "Guidelines:\n" +
                    "1. Be helpful and concise\n" +
                    "2. Give step-by-step solutions\n" +
                    "3. If unsure, suggest contacting support\n" +
                    "4. Use friendly professional tone\n\n" +
                    "Response:";

    /**
     * SUMMARIZATION PROMPT - For summarizing ticket conversations
     * Used when: Ticket has 5+ comments
     */
    public static final String SUMMARIZE_PROMPT =
            "Summarize this IT support ticket conversation:\n\n" +
                    "Ticket: %s\n\n" +
                    "Conversation:\n%s\n\n" +
                    "Provide a brief summary (2-3 sentences) covering:\n" +
                    "1. Main issue\n" +
                    "2. Actions taken\n" +
                    "3. Current status\n\n" +
                    "Summary:";

    /**
     * SUPPORT ADVICE PROMPT - For troubleshooting guidance
     * Used when: Support agent needs help
     */
    public static final String SUPPORT_ADVICE_PROMPT =
            "You are a senior IT support engineer. Help with:\n\n" +
                    "Issue: %s\n" +
                    "Category: %s\n" +
                    "Priority: %s\n\n" +
                    "Provide:\n" +
                    "1. 3-5 troubleshooting steps\n" +
                    "2. Common causes\n" +
                    "3. When to escalate\n\n" +
                    "Response:";


    /**
     * CHATBOT PROMPT - With Knowledge Base Context (RAG)
     */
    public static final String CHATBOT_WITH_KB_PROMPT =
            "You are an IT support assistant. Use the following knowledge base articles to help answer the user's question.\n\n" +
                    "=== KNOWLEDGE BASE ARTICLES ===\n%s\n" +
                    "=== END OF KNOWLEDGE BASE ===\n\n" +
                    "User Question: %s\n\n" +
                    "Instructions:\n" +
                    "1. Use the information from the knowledge base articles to answer\n" +
                    "2. If the articles don't fully answer, supplement with your knowledge\n" +
                    "3. Be helpful, concise, and professional\n" +
                    "4. Provide step-by-step instructions when applicable\n\n" +
                    "Response:";
}
