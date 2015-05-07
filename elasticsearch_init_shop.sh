#!/bin/sh

# initialize shop index.
curl -XPUT "http://localhost:9200/shop"

sleep 2

# _close index
curl -XPOST "http://localhost:9200/shop/_close"

sleep 2

# setting analysis 
curl -XPUT 'http://localhost:9200/shop/_settings' -d '{"settings":{"index":{"analysis":{"analyzer":{"my_search_analyzer":{"tokenizer":"standard","filter":["standard","asciifolding","lowercase","kstem","search_synonym"]},"my_search_std_analyzer":{"tokenizer":"standard","filter":["standard","asciifolding","lowercase","kstem"]},"my_ngram_analyzer":{"tokenizer":"my_ngram_tokenizer","filter":["standard","asciifolding","lowercase","kstem","search_synonym"]}},"tokenizer":{"my_ngram_tokenizer":{"type":"nGram","min_gram":"2","max_gram":"16","token_chars":["letter"]}},"filter":{"search_synonym":{"type":"synonym","synonyms_path":"analysis/synonyms/search.sym"}}}}}}'

sleep 2

# _open index
curl -XPOST "http://localhost:9200/shop/_open"

sleep 2

# setting schema mapping
curl -XPUT 'http://localhost:9200/shop/_mapping/okmall' -d '{"okmall":{"properties":{"product_name":{"type":"string","analyzer":"my_ngram_analyzer"},"brand_name":{"type":"string","analyzer":"my_search_std_analyzer"},"url":{"type":"string"},"thumb_url":{"type":"string"},"name":{"type":"string"},"org_price":{"type":"integer"},"sale_price":{"type":"integer"},"cp":{"type":"string","analyzer":"my_search_analyzer"},"keyword":{"type":"string","analyzer":"my_search_analyzer"}}}}'

#end

