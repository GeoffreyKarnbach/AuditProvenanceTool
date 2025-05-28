def question_rewriter(question, patterns):
    result_question = remove_hyphens_in_words(question)

    for pattern in patterns:
        if pattern[0] == "verb_then_pcomp":
            result_question = handle_verb_then_pcomp_pattern(question, pattern[1])

    return result_question

def handle_verb_then_pcomp_pattern(question, pattern):
    to_remove_positions = pattern[0]
    replacement_veb = pattern[2].replace("ing", "ed")
    questions_words = str(question).split(" ")

    for pos in reversed(to_remove_positions):
        del questions_words[pos]

    questions_words.insert(to_remove_positions[0], replacement_veb)
    return " ".join(questions_words)

def remove_hyphens_in_words(question):
    words = question.split(" ")
    for i, word in enumerate(words):
        if "-" in word and (i == 0 or words[i - 1] != "") and (i == len(words) - 1 or words[i + 1] != ""):
            words[i] = word.replace("-", "")

    return " ".join(words)