def check_for_common_patterns(tokens):
    patterns = []

    for pattern_checker in registered_patterns:
        result = pattern_checker[1](tokens)
        if result:
            patterns.append((pattern_checker[0], result))

    return patterns

# Check for pattern of type "VERB in PCOMP"
# for instance: involved in processing => processed
def check_for_verb_followed_by_pcomp(tokens):
    for i, token in enumerate(tokens):
        if token.dep_ == "ROOT" and token.pos_ == "VERB":
            # Check if the position before the verb is like "was" or "is"
            aux_before = False
            if i > 0 and tokens[i - 1].dep_ == "auxpass" and tokens[i - 1].pos_ == "AUX":
                aux_before = True

            if i + 1 < len(tokens) and tokens[i + 1].dep_ == "prep" and tokens[i + 1].pos_ == "ADP":
                # Search for the next potential pcomp and add it to the tuple of patterns (can be multiple positions far)
                found = False
                for j in range(i + 2, len(tokens)):
                    if tokens[j].dep_ == "pcomp" and tokens[j].pos_ == "VERB":
                        if aux_before:
                            return (i-1, i, i+1, j), f"{tokens[i - 1].text} {token.text} {tokens[i + 1].text}", tokens[j].text

                        return (i, i+1, j), f"{token.text} {tokens[i + 1].text}", tokens[j].text

                if not found:
                    return (i, i+1), f"{token.text} {tokens[i + 1].text}", None

    return None


registered_patterns = [
    ("verb_then_pcomp", check_for_verb_followed_by_pcomp)
]