<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2022. Bernard Bou
  -->

<!-- GXL 2 TREEBOLIC - 14/05/2008 - 10:42:49 (C) 2004-2008 Author: Bernard Bou -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xlink="http://www.w3.org/1999/xlink"
	exclude-result-prefixes="xlink">
	<xsl:output method="xml" indent="yes" encoding="UTF-8" omit-xml-declaration="no" doctype-system="Treebolic.dtd" />

	<xsl:template match="/">
		<xsl:element name="treebolic">
			<xsl:apply-templates select="//graph/attr[starts-with(@name,'treebolic-')]">
				<xsl:with-param name="context">
					treebolic
				</xsl:with-param>
			</xsl:apply-templates>
			<xsl:element name="tree">
				<xsl:apply-templates select="//graph/attr[starts-with(@name,'tree-')]">
					<xsl:with-param name="context">
						tree
					</xsl:with-param>
				</xsl:apply-templates>
				<xsl:element name="nodes">
					<xsl:apply-templates select="//graph/attr[starts-with(@name,'nodes-')][not(contains(@name,'default'))]">
						<xsl:with-param name="context">
							nodes
						</xsl:with-param>
					</xsl:apply-templates>
					<xsl:element name="default.treeedge">
						<xsl:apply-templates select="//graph/attr[starts-with(@name,'nodes-default-treeedge')]">
							<xsl:with-param name="context">
								nodes-default-treeedge
							</xsl:with-param>
						</xsl:apply-templates>
					</xsl:element>
					<xsl:apply-templates select="//node[@id='root']" />
				</xsl:element>
				<xsl:element name="edges">
					<xsl:apply-templates select="//graph/attr[starts-with(@name,'edges-')][not(contains(@name,'default'))]">
						<xsl:with-param name="context">
							edges
						</xsl:with-param>
					</xsl:apply-templates>
					<xsl:element name="default.edge">
						<xsl:apply-templates select="//graph/attr[starts-with(@name,'edges-default-edge')]">
							<xsl:with-param name="context">
								edges-default-edge
							</xsl:with-param>
						</xsl:apply-templates>
					</xsl:element>
					<xsl:apply-templates select="//edge[type/@xlink:href='schema.xml#NonTreeEdge']" />
				</xsl:element>
			</xsl:element>
			<xsl:element name="tools">
				<xsl:element name="menu">
					<xsl:for-each select="//graph/attr[@name = 'menuitem-action']/tup/*">
						<xsl:element name="menuitem">
							<xsl:variable name="i">
								<xsl:value-of select="position()" />
							</xsl:variable>
							<xsl:variable name="a">
								<xsl:value-of select="./text()" />
							</xsl:variable>
							<xsl:attribute name="action">
                                <xsl:value-of select="$a" />
                            </xsl:attribute>
							<xsl:if test="$a = 'search'">
								<xsl:attribute name="match-target">
                                    <xsl:value-of select="//graph/attr[@name = 'menuitem-match-target']/tup/string[$i]/text()" />
                                </xsl:attribute>
								<xsl:attribute name="match-scope">
                                    <xsl:value-of select="//graph/attr[@name = 'menuitem-match-scope']/tup/string[$i]/text()" />
                                </xsl:attribute>
								<xsl:attribute name="match-mode">
                                    <xsl:value-of select="//graph/attr[@name = 'menuitem-match-mode']/tup/string[$i]/text()" />
                                </xsl:attribute>
							</xsl:if>
							<xsl:element name="label">
								<xsl:value-of select="//graph/attr[@name = 'menuitem-label']/tup/string[$i]/text()" />
							</xsl:element>
							<xsl:variable name="l">
								<xsl:value-of select="//graph/attr[@name = 'menuitem-link']/tup/locator[$i]/@xlink:href" />
							</xsl:variable>
							<xsl:if test="$l != ''">
								<xsl:element name="a">
									<xsl:attribute name="href">
                                        <xsl:value-of select="$l" />
                                    </xsl:attribute>
								</xsl:element>
							</xsl:if>
						</xsl:element>
					</xsl:for-each>
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>

	<xsl:template match="node">
		<xsl:element name="node">
        	<!-- id -->
			<xsl:variable name="id">
				<xsl:value-of select="@id" />
			</xsl:variable>
			<!-- id attribute -->
			<xsl:attribute name="id">
                <xsl:value-of select="$id" />
            </xsl:attribute>
        	<!-- other attributes -->
			<xsl:apply-templates
				select="./attr[@name != 'label' and @name != 'content' and @name != 'img-src' and @name != 'link' and not(starts-with(@name,'mountpoint'))]" />
			<!-- label -->
			<xsl:apply-templates select="./attr[@name = 'label']" />
			<!-- content -->
			<xsl:apply-templates select="./attr[@name = 'content']" />
			<!-- treeedge -->
			<xsl:for-each select="//edge[@to=$id][type/@xlink:href='schema.xml#TreeEdge']">
				<xsl:if test="count(./attr) != 0">
					<xsl:element name="treeedge">
						<xsl:apply-templates select="./attr" />
					</xsl:element>
				</xsl:if>
			</xsl:for-each>
			<!-- img -->
			<xsl:apply-templates select="./attr[@name = 'img-src']" />
       		<!-- link -->
			<xsl:apply-templates select="./attr[@name = 'link']" />
			<!-- mountpoint -->
			<xsl:apply-templates select="./attr[@name = 'mountpoint']" />
			<!-- child nodes -->
			<xsl:for-each select="//edge[@from=$id][type/@xlink:href='schema.xml#TreeEdge']">
				<xsl:variable name="child">
					<xsl:value-of select="@to" />
				</xsl:variable>
				<!-- <xsl:message>tree edge: <xsl:value-of select="id($child)/attr[@name='label']/string"/> to <xsl:value-of select="./attr[@name='label']/string"/></xsl:message> -->
				<xsl:apply-templates select="id($child)" />
			</xsl:for-each>
		</xsl:element>
	</xsl:template>

	<xsl:template match="edge">
		<xsl:element name="edge">
			<xsl:attribute name="from">
                <xsl:value-of select="@from" />
            </xsl:attribute>
			<xsl:attribute name="to">
                <xsl:value-of select="@to" />
            </xsl:attribute>
			<xsl:apply-templates select="./attr" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="attr">
		<xsl:param name="context" />
		<xsl:choose>
			<xsl:when test="@name='label'">
				<xsl:element name="label">
					<xsl:value-of select="./string/text()" />
				</xsl:element>
			</xsl:when>
			<xsl:when test="@name='content'">
				<xsl:element name="content">
					<xsl:value-of select="./string/text()" />
				</xsl:element>
			</xsl:when>
			<xsl:when test="@name='img-src'">
				<xsl:element name="img">
					<xsl:attribute name="src">
                        <xsl:value-of select="./locator/@xlink:href" />
                    </xsl:attribute>
				</xsl:element>
			</xsl:when>
			<xsl:when test="@name=concat($context,'-img-src')">
				<xsl:element name="img">
					<xsl:attribute name="src">
                        <xsl:value-of select="./locator/@xlink:href" />
                    </xsl:attribute>
				</xsl:element>
			</xsl:when>
			<xsl:when test="@name='link'">
				<xsl:element name="a">
					<xsl:attribute name="href">
                        <xsl:value-of select="./locator/@xlink:href" />
                    </xsl:attribute>
				</xsl:element>
			</xsl:when>
			<xsl:when test="@name='mountpoint'">
				<xsl:element name="mountpoint">
					<xsl:attribute name="weight">
                        <xsl:value-of select="../attr[@name = 'mountpoint-weight']/string/text()" />
                    </xsl:attribute>
					<xsl:attribute name="delaymount">
                        <xsl:value-of select="../attr[@name = 'mountpoint-delaymount']/string/text()" />
                    </xsl:attribute>
					<xsl:element name="a">
						<xsl:attribute name="href">
                            <xsl:value-of select="./locator/@xlink:href" />
                        </xsl:attribute>
					</xsl:element>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="attrname">
					<xsl:choose>
						<xsl:when test="$context != ''">
							<xsl:value-of select="substring-after(string(./@name),concat($context,'-'))" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="string(./@name)" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				<xsl:attribute name="{string($attrname)}">
                    <xsl:value-of select="./string/text()" />
                </xsl:attribute>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="text()">
		<xsl:value-of select="normalize-space()" />
	</xsl:template>

</xsl:stylesheet>
