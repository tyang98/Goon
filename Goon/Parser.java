package Goon;

import java.util.List;
import static Goon.TokenType.*;
import java.util.ArrayList;

class Parser {

  private static class ParseError extends RuntimeException {
  }

  private final List<Token> tokens;
  private int current = 0;

  Parser(List<Token> tokens) {
    this.tokens = tokens;
  }

  private Expr expression() {
    return equality();
  }

  private Expr equality() {
    Expr expr = comparison();

    while (match(BANG_EQUAL, EQUAL_EQUAL)) {
      Token operator = previous();
      // parse right-hand operand
      Expr right = comparison();
      expr = new Expr.Binary(expr, operator, right);
    }
    return expr;
  }

  private Expr comparison() {
    Expr expr = addition();

    while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
      Token operator = previous();
      Expr right = comparison();
      expr = new Expr.Binary(expr, operator, right);
    }
    return expr;
  }

  private Expr addition() {
    Expr expr = multiplication();

    while (match(MINUS, PLUS)) {
      Token operator = previous();
      Expr right = comparison();
      expr = new Expr.Binary(expr, operator, right);
    }
    return expr;
  }

  private Expr multiplication() {
    Expr expr = unary();

    while (match(SLASH, STAR)) {
      Token operator = previous();
      Expr right = comparison();
      expr = new Expr.Binary(expr, operator, right);
    }
    return expr;
  }

  private Expr unary() {
    if (match(BANG, MINUS)) {
      Token operator = previous();
      Expr right = unary();
      return new Expr.Unary(operator, right);
    }
    return primary();
  }

  private Expr primary() {
    if (match(FALSE))
      return new Expr.Literal(false);
    if (match(TRUE))
      return new Expr.Literal(true);
    if (match(NIL))
      return new Expr.Literal(null);

    if (match(NUMBER, STRING)) {
      return new Expr.Literal(previous().literal);
    }

    if (match(IDENTIFIER)) {
      return new Expr.Variable(previous());
    }

    if (match(LEFT_PAREN)) {
      Expr expr = expression();
      consume(RIGHT_PAREN, "Expect ')' after expression.");
      return new Expr.Grouping(expr);
    }

    throw error(peek(), "Expect expression.");
  }

  private void synchronize() {
    advance();

    while (!isAtEnd()) {
      if (previous().type == SEMICOLON)
        return;

      switch (peek().type) {
        case CLASS:
        case FUN:
        case BOOMER:
        case FOR:
        case IF:
        case WHILE:
        case PRINT:
        case RETURN:
          return;
      }

      advance();
    }
  }

  List<Stmt> parse() {
    List<Stmt> statements = new ArrayList<>();
    while (!isAtEnd()) {
      statements.add(declaration());
    }
    return statements;
  }

  private Stmt declaration() {
    try {
      if (match(BOOMER))
        return boomerDeclaration();
      return statement();
    } catch (ParseError error) {
      synchronize();
      return null;
    }
  }

  private Stmt boomerDeclaration() {
    Token name = consume(IDENTIFIER, "Expect variable name.");

    Expr initializer = null;
    if (match(EQUAL)) {
      initializer = expression();
    }

    consume(SEMICOLON, "Expect ';' after variable declaration.");
    return new Stmt.Boomer(name, initializer);
  }

  private Stmt statement() {
    if (match(PRINT))
      return printStatement();

    return expressionStatement();
  }

  private Stmt printStatement() {
    Expr value = expression();
    consume(SEMICOLON, "Expect ';' after value.");
    return new Stmt.Print(value);
  }

  private Stmt expressionStatement() {
    Expr expr = expression();
    consume(SEMICOLON, "Expect ';' after expression.");
    return new Stmt.Expression(expr);
  }

  private Token consume(TokenType type, String message) {
    if (check(type))
      return advance();
    throw error(peek(), message);
  }

  private ParseError error(Token token, String message) {
    Goon.error(token, message);
    return new ParseError();
  }

  private boolean match(TokenType... types) {
    for (TokenType type : types) {
      if (check(type)) {
        advance();
        return true;
      }
    }
    return false;
  }

  private boolean check(TokenType type) {
    if (isAtEnd())
      return false;
    return peek().type == type;
  }

  private Token advance() {
    if (!isAtEnd())
      current++;
    return previous();
  }

  // checks if tokens have run out to parse
  private boolean isAtEnd() {
    return peek().type == EOF;
  }

  // returns current token we have yet to consume
  private Token peek() {
    return tokens.get(current);
  }

  // returns the most recentl consumed token
  private Token previous() {
    return tokens.get(current - 1);
  }
}