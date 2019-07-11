<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet
  xmlns:common="http://exslt.org/common"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <xsl:output method="text" />

<!-- 
xMake an integer in the output describing the passed type's size. 
-->
  <xsl:template name="tplMakeTypeSize">
    <xsl:param name="typeName" />
     
    <!-- Debug output. 
    <xsl:text>tplMakeTypeSize(</xsl:text>
    <xsl:text><xsl:value-of select="$typeName"/></xsl:text>
    <xsl:text>) 
    </xsl:text>-->

    <!-- <xsl:variable> 
        name="sizeInBytes"
        select="DATATYPES/*[@id=$typeName]/CVALUETYPE" />
    </xsl:variable> -->    
    <xsl:variable 
        name="cvalueType"
        select="/*/*/DATATYPES/*[@id=$typeName]/CVALUETYPE" />
    <!-- <xsl:variable 
        name="bitLength"
        select="$cvalueType/@bl" /> -->
    <xsl:variable 
        name="maxSize"
        select="$cvalueType/@maxsz" />
     <xsl:text><xsl:value-of select="$maxSize"/></xsl:text>
  </xsl:template>

<!-- 
Make the initialized structure buffer:
private byte [] _buffer = new byte[ n]{ 0, .. , }; 
-->
  <xsl:template name="tplMakeBuffer">
    <xsl:param name="typeName" />

     <!-- Debug output. -->
    <xsl:text>// tplMakeBuffer(</xsl:text>
    <xsl:text><xsl:value-of select="$typeName"/></xsl:text>
    <xsl:text>) 
    </xsl:text>

    <xsl:variable 
        name="structureElements">
    <xsl:for-each 
        select="STRUCTURE/DATAOBJ">
        <xsl:copy-of select="." />
    </xsl:for-each>
    </xsl:variable >

    <xsl:text>private final byte[] _buffer = new byte[</xsl:text>
    <xsl:for-each 
           select="common:node-set($structureElements)/child::*">          
    <xsl:text>+</xsl:text>

             <xsl:call-template name='tplMakeTypeSize'>
             <xsl:with-param name='typeName' select="@dtref"/>
	     </xsl:call-template>
      </xsl:for-each>
    <xsl:text>];</xsl:text>
  </xsl:template>

<!-- 
Make the initializer:
{
}
-->
  <xsl:template name="tplMakeInitializer">
    <xsl:param name="typeName" />

     <!-- Debug output. -->
    <xsl:text>// tplMakeInitializer(</xsl:text>
    <xsl:text><xsl:value-of select="$typeName"/></xsl:text>
    <xsl:text>) 
    </xsl:text>

    <xsl:variable 
        name="structureElements">
    <xsl:for-each 
        select="STRUCTURE/DATAOBJ">
        <xsl:copy-of select="." />
    </xsl:for-each>
    </xsl:variable >

    <xsl:text>
    {
        int currentOffset = 0;</xsl:text>
    <xsl:for-each 
           select="common:node-set($structureElements)/child::*">

     <xsl:if test="not(string(@def))">
        // Empty.</xsl:if>
     <xsl:if test="string(@def)">
    <xsl:text>
        copyTo( <xsl:value-of select="translate(@def,'()','')"/></xsl:text>
    <xsl:text> );</xsl:text>
      </xsl:if>

    <xsl:text>
        currentOffset += </xsl:text>
             <xsl:call-template name='tplMakeTypeSize'>
             <xsl:with-param name='typeName' select="@dtref"/>
	     </xsl:call-template>;
      </xsl:for-each>
    <xsl:text>
    }</xsl:text>

    <!-- <xsl:variable> 
        name="sizeInBytes"
        select="DATATYPES/*[@id=$typeName]/CVALUETYPE" />
    </xsl:variable> -->    
    <xsl:variable 
        name="cvalueType"
        select="/*/*/DATATYPES/*[@id=$typeName]/CVALUETYPE" />
    <xsl:variable 
        name="bitLength"
        select="$cvalueType/@bl" />
    <xsl:variable 
        name="maxSize"
        select="$cvalueType/@maxsz" />
     <xsl:text><xsl:value-of select="$maxSize"/></xsl:text>
  </xsl:template>

<!-- xxxxxxxxxxxxxxxxxxxxxxxxxxxx Offset list ... -->
  <xsl:template name="tplMakeOffsetList">
    <xsl:param name="typeName" />

     <!-- Debug output. -->
    <xsl:text>// tplMakeOffsetList(</xsl:text>
    <xsl:text><xsl:value-of select="$typeName"/></xsl:text>
    <xsl:text>) 
    </xsl:text>

    <xsl:variable 
        name="structureElements">
    <xsl:for-each 
        select="STRUCTURE/DATAOBJ">
        <xsl:copy-of select="." />
    </xsl:for-each>
    </xsl:variable >

    <xsl:text>
     private static int SIZE = 0;
    </xsl:text>
    <xsl:for-each 
           select="common:node-set($structureElements)/child::*">

    <xsl:text>private static final int Offset$</xsl:text>
    <xsl:text><xsl:value-of select="QUAL"/></xsl:text>
    <xsl:text> = SIZE  += </xsl:text>
                 <xsl:call-template name='tplMakeTypeSize'>
             <xsl:with-param name='typeName' select="@dtref"/>
	     </xsl:call-template>
    <xsl:text>;
    </xsl:text>

      </xsl:for-each>
    <xsl:text>
    }</xsl:text>

    <!-- <xsl:variable> 
        name="sizeInBytes"
        select="DATATYPES/*[@id=$typeName]/CVALUETYPE" />
    </xsl:variable> -->    
    <xsl:variable 
        name="cvalueType"
        select="/*/*/DATATYPES/*[@id=$typeName]/CVALUETYPE" />
    <xsl:variable 
        name="bitLength"
        select="$cvalueType/@bl" />
    <xsl:variable 
        name="maxSize"
        select="$cvalueType/@maxsz" />
     <xsl:text><xsl:value-of select="$maxSize"/></xsl:text>
  </xsl:template>

  <xsl:template match="DIDS">
    <xsl:text>/**</xsl:text>
    <xsl:text>&#xa;</xsl:text>
    <xsl:text> * Generated.</xsl:text>
    <xsl:text>&#xa;</xsl:text>
    <xsl:text> */</xsl:text>
    <xsl:text>&#xa;</xsl:text>
    <xsl:text>public class Dids {</xsl:text>
    <xsl:text>&#xa;</xsl:text>
    <xsl:apply-templates/>
    <xsl:text>&#xa;</xsl:text>
    <xsl:text>}</xsl:text>
    <xsl:text>&#xa;</xsl:text>
  </xsl:template>

  <!--
Creates an inner class per DID.
     -->
  <xsl:template match="DIDS/DID">
    <xsl:variable 
        name="className"
        select="./QUAL" />

    <!-- Class javadoc. -->
    <xsl:text>/** </xsl:text>
    <xsl:text><xsl:value-of select="./NAME/TUV" /></xsl:text>
    <xsl:text> */</xsl:text>
    <xsl:text>&#xa;</xsl:text>

    <!-- Class body. -->
    <xsl:text>public static class </xsl:text>
    <xsl:text><xsl:value-of select='$className' /></xsl:text>
    <xsl:text> {&#xa;</xsl:text>

    <!-- Create base buffer. -->
    <xsl:text>
    /** 
     * The buffer holding the structure data.
     */
    </xsl:text>
    <xsl:call-template name='tplMakeBuffer'>
             <xsl:with-param name='typeName' select='QUAL' />
    </xsl:call-template>
    <xsl:text>

    /** 
     *  Instance initializer.
     */
    </xsl:text>
    <xsl:call-template name='tplMakeOffsetList'>
             <xsl:with-param name='typeName' select='QUAL' />
    </xsl:call-template>
    <xsl:call-template name='tplMakeInitializer'>
             <xsl:with-param name='typeName' select='QUAL' />
    </xsl:call-template>

    <xsl:text>


    /**
     * Compare the Diagnostic identifiers.
     * @return true if the DIDs hold the same content.
     */
    public boolean isEquivalent( <xsl:value-of select="QUAL"/></xsl:text>
    <xsl:text> other ) {
        return 0 == Array.compare( other._buffer, _buffer );
    }

    </xsl:text>

    <xsl:apply-templates/>

    <!-- The content buffer -->
    <xsl:text>--content--</xsl:text>

    <!-- Constructor. -->
    <xsl:text>&#xa;  public </xsl:text>
    <xsl:text><xsl:value-of select='$className' /></xsl:text>
    <xsl:text>( byte[] data ) {
        System.arraycopy( data, 0, _buffer, 0, _buffer.length );
    }
}

</xsl:text>
  </xsl:template>

<!--
Create a property.
-->
  <xsl:template match="DIDS/DID/STRUCTURE/DATAOBJ">
    <xsl:text> DO  final byte[] <xsl:value-of select="./QUAL" /></xsl:text>
    <xsl:text> = new  byte[</xsl:text>
    <xsl:text><xsl:call-template name='tplMakeTypeSize'>
             <xsl:with-param name='typeName' select='@dtref' />
        </xsl:call-template></xsl:text>
    <xsl:text>];</xsl:text>
    <xsl:text>&#xa;</xsl:text>
  </xsl:template>

  <xsl:template match="@*|node()">
    <xsl:apply-templates select="@*|node()" />
  </xsl:template>
</xsl:stylesheet>
