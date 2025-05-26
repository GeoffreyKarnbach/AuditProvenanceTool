import spacy
import pattern_checker
import question_handler
import question_rewriter

nlp = spacy.load("en_core_web_sm")

def handle_questions(questions):
    handled_questions = []
    for line in questions.split("\n"):
        line = line.strip()
        if line:
            handled_questions.append(handle_question(line))

    return handled_questions


def handle_question(question):

    print(f"Handling question: {question}")

    doc = nlp(question)
    for token in doc:
        print(f"{token.text:<15} {token.dep_:<10} {token.pos_:<10} {token.head.text}")

    patterns = pattern_checker.check_for_common_patterns(doc)
    print(f"Patterns: {patterns}")

    question = question_rewriter.question_rewriter(doc, patterns)
    print(f"Rewritten question: {question}")

    return question


if __name__ == "__main__":
    with open("sample/pq.txt", "r") as file:
        sample_questions = file.read()
    print(handle_questions(sample_questions))
