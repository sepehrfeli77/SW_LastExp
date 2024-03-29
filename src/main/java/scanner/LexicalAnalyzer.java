package scanner;

import error_handler.ErrorHandlerHelper;
import scanner.token.Token;
import scanner.type.Type;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LexicalAnalyzer {

    private Matcher matcher;

    public Matcher getMatcher() {
        return matcher;
    }

    public LexicalAnalyzer(java.util.Scanner sc) {
        StringBuilder input = new StringBuilder();
        while (sc.hasNext()) {
            input.append(sc.nextLine());
        }
        StringBuilder tokenPattern = new StringBuilder();
        for (Type tokenType : Type.values()) {
            tokenPattern.append(String.format("|(?<%s>%s)", tokenType.name(), tokenType.pattern));
        }
        Pattern expression = Pattern.compile(tokenPattern.substring(1));
        matcher = expression.matcher(input.toString());
    }

    public Token getNextToken() {

        while (getMatcher().find()) {
            for (Type t : Type.values()) {

                if (getMatcher().group(t.name()) != null) {
                    if (getMatcher().group(Type.COMMENT.name()) != null) {
                        break;

                    }
                    if (getMatcher().group(Type.ErrorID.name()) != null) {
                        ErrorHandlerHelper.printError("The id must start with character");
                        break;
                    }

                    return new Token(t, getMatcher().group(t.name()));
                }
            }
        }
        return new Token(Type.EOF, "$");
    }
}
