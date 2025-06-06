import spacy
import pattern_checker
import question_rewriter
import question_formalizer
import prov_mapper

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

    rewritten_question = question_rewriter.question_rewriter(question, patterns)
    print(f"Rewritten question: {rewritten_question}")

    formalized_question = question_formalizer.formalize_question(rewritten_question)
    print(f"Formalized question: {formalized_question}")

    target_concept, mapped_to_prov_o = prov_mapper.map_formalized_question_to_prov_o(formalized_question)
    print(f"Mapped to PROV-O: {mapped_to_prov_o}")

    response_object = {
        "original_question": question,
        "tokens": [(token.text, token.dep_, token.pos_, token.head.text) for token in doc],
        "patterns": patterns,
        "rewritten_question": rewritten_question,
        "formalized_question": formalized_question,
        "target_concept": target_concept,
        "mapped_to_prov_o": mapped_to_prov_o
    }

    return response_object

if __name__ == "__main__":
    with open("sample/pq.txt", "r") as file:
        sample_questions = file.read()

    for question in handle_questions(sample_questions):
        print(question)
