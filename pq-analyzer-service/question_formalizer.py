import spacy

nlp = spacy.load("en_core_web_sm")

def get_question_phrase(doc):
    if doc[0].text.lower() == "how":
        if len(doc) > 1 and doc[1].pos_ in ("ADV", "ADJ", "NUM"):
            return f"{doc[0].text.lower()} {doc[1].text.lower()}"
        return "how"
    elif doc[0].tag_ in ("WP", "WDT", "WRB"):
        return doc[0].text.lower()
    return "?"

def formalize_question(question: str):
    doc = nlp(question)

    subject = get_question_phrase(doc)
    predicate = None
    object_parts = []

    for token in doc:
        if token.dep_ == "ROOT":
            if token.pos_ == "VERB":
                predicate = token.lemma_.lower()
            elif token.pos_ == "AUX" and token.lemma_ in ("be", "have"):
                predicate = token.head.lemma_.lower()  # fallback
            elif token.pos_ == "NOUN":
                predicate = token.lemma_.lower()
            break

    for chunk in doc.noun_chunks:
        if subject and subject in chunk.text.lower():
            continue
        object_parts.append(chunk.text.strip())

    obj = " ".join(object_parts).lower().strip()
    obj = " ".join([word for word in obj.split(" ") if word not in ("the", "a", "an")])

    return subject or "?", predicate or "?", obj or "?"
