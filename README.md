OpenNLP with NKJP corpus support
=========

This branch adds support for NKJP (Narodowy Korpus Języka Polskiego) corpus format. NKJP million sub-corpus is the only manually annotated corpus containing Part-Of-Speech annotation available for polish language at the moment. You can find the sub-corpus [here](http://clip.ipipan.waw.pl/LRT?action=AttachFile&do=get&target=NKJP-PodkorpusMilionowy-1.1.tgz). Keep in mind that NKJP corpus is released under GNU GPL v.3 (whereas OpenNLP is released under Apache 2.0 license). 

For now there's only support for extracting Part-Of-Speech information from the corpus which allows training POSTagger. 

Assuming you extracted corpus to path ~/NKJP-PodkorpusMilionowy-1.0, this branch allows you to run the following commands:

```bash
opennlp POSTaggerTrainer.nkjp -lang pl -model nkjp_short_pos_model.bin -data ~/NKJP-PodkorpusMilionowy-1.0/ -tagset nkjp
opennlp POSTaggerTrainer.nkjp -lang pl -model nkjp_full_pos_model.bin -data ~/NKJP-PodkorpusMilionowy-1.0/ -tagset nkjp_full
opennlp POSTaggerTrainer.nkjp -lang pl -model universal_pos_model.bin -data ~/NKJP-PodkorpusMilionowy-1.0/ -tagset universal

opennlp POSTaggerCrossValidator.nkjp -lang pl -data ~/NKJP-PodkorpusMilionowy-1.0/ -tagset nkjp
opennlp POSTaggerCrossValidator.nkjp -lang pl -data ~/NKJP-PodkorpusMilionowy-1.0/ -tagset nkjp_full
opennlp POSTaggerCrossValidator.nkjp -lang pl -data ~/NKJP-PodkorpusMilionowy-1.0/ -tagset universal

opennlp POSTaggerConverter nkjp -tagset nkjp -data ~/NKJP-PodkorpusMilionowy-1.0/ -encoding utf8 > output.txt
opennlp POSTaggerConverter nkjp -tagset nkjp_full -data ~/NKJP-PodkorpusMilionowy-1.0/ -encoding utf8 > output.txt
opennlp POSTaggerConverter nkjp -tagset universal -data ~/NKJP-PodkorpusMilionowy-1.0/ -encoding utf8 > output.txt
```

As for available tags:
* nkjp - only base gramatical class from nkjp available [tagset](http://nkjp.pl/poliqarp/help/ense2.html). For example subst or adj.
* nkjp_full - whole original positional tagset as in original corpus. For example subst:pl:acc:n. Beside the base gramatical class on the first position, it includes in the following positions information such as gender, number, case etc.
* universal - concept taken from [universal-pos-tags](https://code.google.com/p/universal-pos-tags), though had to add couple mappings. You can see full map from nkjp to unviersal in the source [code](https://github.com/SentiOne/opennlp/blob/9c4e735fd9e05621a04a3833b922655ac9c49684/opennlp-tools/src/main/java/opennlp/tools/formats/NkjpPOSSampleStream.java#L133)

**Caution!** For testing purposes, part of the corpus is included in resources. This probably violates Apache 2.0 license. Later the tast data will be replaced with some custom data with the same format.
